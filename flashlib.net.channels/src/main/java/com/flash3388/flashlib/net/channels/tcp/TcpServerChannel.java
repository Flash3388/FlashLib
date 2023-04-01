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

public class TcpServerChannel implements NetServerChannel {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;
    private final UpdateHandler mHandler;

    private final Map<SocketAddress, ClientNode> mClients;
    private final ByteBuffer mReadBuffer;

    private ServerSocketChannel mUnderlyingChannel;
    private Selector mSelector;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger, UpdateHandler handler) {
        mBindAddress = bindAddress;
        mLogger = logger;
        mHandler = handler;

        mClients = new ConcurrentHashMap<>();
        mReadBuffer = ByteBuffer.allocateDirect(1024);
        mUnderlyingChannel = null;
        mSelector = null;
    }

    @Override
    public void processUpdates() throws IOException {
        openChannel();

        while (!Thread.interrupted()) {
            int available = mSelector.selectNow();
            if (available < 1) {
                return;
            }

            for (Iterator<SelectionKey> it = mSelector.selectedKeys().iterator(); it.hasNext();) {
                SelectionKey key = it.next();

                try {
                    if (key.isAcceptable()) {
                        // new client
                        handleNewClient();
                    } else if (key.isReadable()) {
                        // new data from client

                        //noinspection resource
                        SocketAddress address = ((SocketChannel) key.channel()).getRemoteAddress();
                        ClientNode clientNode = mClients.get(address);
                        if (clientNode == null) {
                            // no such client
                            mLogger.error("Received data from unknown client at {}", address);
                            continue;
                        }

                        handleClientNewData(clientNode);
                    }
                } finally {
                    it.remove();
                }
            }
        }
    }

    @Override
    public void writeToAll(ByteBuffer buffer) throws IOException {
        writeToAllBut(buffer, null);
    }

    @Override
    public void writeToOne(ByteBuffer buffer, NetClientInfo clientInfo) throws IOException {
        ClientNode node = mClients.get(clientInfo.getAddress());
        if (node == null) {
            throw new IllegalArgumentException("no such client: " + clientInfo.getAddress());
        }

        try {
            mLogger.debug("Writing message to client: {}, size={}",
                    clientInfo.getAddress(),
                    buffer.limit());
            buffer.rewind();
            node.getChannel().write(buffer);
        } catch (IOException e) {
            mLogger.error("Error sending data to client at {}", node.getClientInfo().getAddress());
            onClientError(node);
            throw e;
        }
    }

    @Override
    public void writeToAllBut(ByteBuffer buffer, NetClientInfo clientToSkip) throws IOException {
        openChannel();

        Set<ClientNode> erroredClients = new HashSet<>();

        ThrowableChain chain = Throwables.newChain();
        for (Iterator<ClientNode> it = mClients.values().iterator();
             it.hasNext(); ) {
            ClientNode node = it.next();
            if (node.getClientInfo().equals(clientToSkip)) {
                continue;
            }

            try {
                mLogger.debug("Writing message to client: {}, size={}",
                        node.getClientInfo().getAddress(),
                        buffer.limit());
                buffer.rewind();
                node.getChannel().write(buffer);
            } catch (IOException e) {
                mLogger.error("Error sending data to client at {}", node.getClientInfo().getAddress());
                chain.chain(e);

                erroredClients.add(node);
            }
        }

        erroredClients.forEach(this::onClientError);

        chain.throwIfType(IOException.class);
    }

    @Override
    public void close() throws IOException {
        if (mUnderlyingChannel != null) {
            Closeables.silentClose(mUnderlyingChannel);
            mUnderlyingChannel = null;
        }

        if (mSelector != null) {
            Closeables.silentClose(mSelector);
            mSelector = null;
        }
    }

    private synchronized void openChannel() throws IOException {
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

    private void handleNewClient() throws IOException {
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

            mHandler.onClientConnected(clientInfo);
        } catch (IOException e) {
            Closeables.silentClose(baseChannel);
        }
    }

    private void handleClientNewData(ClientNode node) {
        NetClientInfo clientInfo = node.getClientInfo();
        try {
            mReadBuffer.clear();

            IncomingData incomingData = node.getChannel().read(mReadBuffer);
            if (incomingData == null || incomingData.getBytesReceived() < 1) {
                onClientError(node);
                return;
            }

            mLogger.debug("Data received from {}: bytes={}",
                    node.getClientInfo().getAddress(),
                    incomingData.getBytesReceived());
            mHandler.onNewData(clientInfo, mReadBuffer, incomingData.getBytesReceived());
        } catch (IOException e) {
            mLogger.error("Error processing new data from client at {}", clientInfo.getAddress());
            onClientError(node);
        }
    }

    private void onClientError(ClientNode node) {
        NetClientInfo clientInfo = node.getClientInfo();

        //noinspection resource
        mClients.remove(clientInfo.getAddress());
        Closeables.silentClose(node);
        mHandler.onClientDisconnected(clientInfo);
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
