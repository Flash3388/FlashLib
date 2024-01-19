package com.flash3388.flashlib.net.channels.tcp;

import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Predicate;

public class TcpServerChannel implements NetServerChannel {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;

    private final Map<SocketAddress, ClientNode> mClients;
    private final Set<ClientNode> mClientsToDisconnect;
    private final ByteBuffer mReadBuffer;

    private ServerSocketChannel mUnderlyingChannel;
    private Selector mSelector;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;

        mClients = new ConcurrentHashMap<>();
        mClientsToDisconnect = new CopyOnWriteArraySet<>();
        mReadBuffer = ByteBuffer.allocateDirect(1024);

        mUnderlyingChannel = null;
        mSelector = null;
    }

    @Override
    public void processUpdates(UpdateHandler updateHandler) throws IOException {
        openChannelIfNecessary();

        disconnectClientList(updateHandler);

        int available = mSelector.selectNow();
        if (available < 1) {
            return;
        }

        for (Iterator<SelectionKey> it = mSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            try {
                if (key.isAcceptable()) {
                    // new client
                    handleNewClient(updateHandler);
                } else if (key.isReadable()) {
                    // new data from client

                    //noinspection resource
                    SocketAddress address = ((SocketChannel) key.channel()).getRemoteAddress();
                    ClientNode node = mClients.get(address);
                    if (node == null) {
                        // no such client
                        mLogger.error("Received data from unknown client at {}", address);
                        continue;
                    }

                    handleClientNewData(node, updateHandler);
                }
            } finally {
                it.remove();
            }
        }
    }

    @Override
    public void writeToAll(ByteBuffer buffer) throws IOException {
        writeToMatching(buffer, null);
    }

    @Override
    public void writeToMatching(ByteBuffer buffer, Predicate<NetClientInfo> predicate) throws IOException {
        Set<ClientNode> erroredClients = new HashSet<>();

        ThrowableChain chain = Throwables.newChain();
        for (ClientNode node : mClients.values()) {
            NetClientInfo clientInfo = node.getClientInfo();

            if (predicate != null && !predicate.test(clientInfo)) {
                continue;
            }

            try {
                mLogger.debug("Writing message to client: {}, size={}",
                        clientInfo.getAddress(),
                        buffer.limit());

                buffer.rewind();
                node.getChannel().write(buffer);
            } catch (IOException e) {
                mLogger.error("Error sending data to client at {}", clientInfo.getAddress());

                chain.chain(e);
                erroredClients.add(node);
            }
        }

        mClientsToDisconnect.addAll(erroredClients);
        chain.throwIfType(IOException.class);
    }

    @Override
    public void close() throws IOException {
        // this is also used to close the channel after an error and re-open it.

        if (mUnderlyingChannel != null) {
            Closeables.silentClose(mUnderlyingChannel);
            mUnderlyingChannel = null;
        }

        if (mSelector != null) {
            Closeables.silentClose(mSelector);
            mSelector = null;
        }

        // TODO: HOW TO UPDATE CLIENTS ON DISCONNECTIONS?
        //      MAYBE THE UPDATEHANDLER NEEDS TO BE SOMETHING SET INSTEAD OF PASSED AS PARAM

        mClients.clear();
        mClientsToDisconnect.clear();
    }

    private void disconnectClientList(UpdateHandler handler) {
        for (Iterator<ClientNode> it = mClientsToDisconnect.iterator();
             it.hasNext(); ) {
            ClientNode node = it.next();
            try {
                disconnectClient(node, handler);
            } finally {
                it.remove();
            }
        }
    }

    private void disconnectClient(ClientNode node, UpdateHandler handler) {
        NetClientInfo clientInfo = node.getClientInfo();

        //noinspection resource
        mClients.remove(clientInfo.getAddress());
        Closeables.silentClose(node);

        try {
            handler.onClientDisconnected(clientInfo);
        } catch (Throwable ignored) {
            // the client is already disconnected, so not much to do here.
        }
    }

    private void handleNewClient(UpdateHandler handler) throws IOException {
        SocketChannel baseChannel = mUnderlyingChannel.accept();

        SocketAddress address = baseChannel.getRemoteAddress();
        mLogger.debug("Server received new client from {} at {}",
                address,
                baseChannel.getLocalAddress());
        try {
            baseChannel.configureBlocking(false);
            baseChannel.register(mSelector, SelectionKey.OP_READ);

            NetClientInfo clientInfo = new NetClientInfo(address);
            NetChannel channel = new TcpChannel(baseChannel);
            ClientNode node = new ClientNode(clientInfo, channel);
            mClients.put(address, node);

            try {
                handler.onClientConnected(clientInfo);
            } catch (Throwable ignored) {
                // we'll ignore this for now, mostly I don't think this is
                // a good enough reason to reset everything.
            }
        } catch (IOException e) {
            Closeables.silentClose(baseChannel);
        }
    }

    private void handleClientNewData(ClientNode node, UpdateHandler handler) {
        NetClientInfo clientInfo = node.getClientInfo();
        try {
            mReadBuffer.clear();

            IncomingData incomingData = node.getChannel().read(mReadBuffer);
            if (incomingData == null || incomingData.getBytesReceived() < 1) {
                disconnectClient(node, handler);
                return;
            }

            mLogger.debug("Data received from {}: bytes={}",
                    clientInfo.getAddress(),
                    incomingData.getBytesReceived());

            handler.onNewData(clientInfo, mReadBuffer, incomingData.getBytesReceived());
        } catch (IOException e) {
            mLogger.error("Error processing new data from client at {}", clientInfo.getAddress());
            disconnectClient(node, handler);
        }
    }

    private synchronized void openChannelIfNecessary() throws IOException {
        if (mUnderlyingChannel == null) {
            try {
                mLogger.debug("Opening new TCP SERVER socket in non-blocking mode");

                mSelector = Selector.open();

                mUnderlyingChannel = ServerSocketChannel.open();
                mUnderlyingChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                mUnderlyingChannel.configureBlocking(false);

                mLogger.debug("Binding TCP SERVER to {}", mBindAddress);
                mUnderlyingChannel.bind(mBindAddress);

                mUnderlyingChannel.register(mSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }

    private static class ClientNode implements Closeable {

        private final NetClientInfo mClientInfo;
        private final NetChannel mChannel;

        private ClientNode(NetClientInfo clientInfo, NetChannel channel) {
            mClientInfo = clientInfo;
            mChannel = channel;
        }

        public NetClientInfo getClientInfo() {
            return mClientInfo;
        }

        public NetChannel getChannel() {
            return mChannel;
        }

        @Override
        public void close() throws IOException {
            mChannel.close();
        }
    }
}
