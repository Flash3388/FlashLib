package com.flash3388.flashlib.net.tcp.impl2;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.net.ConnectedNetChannel;
import com.flash3388.flashlib.net.tcp.ConnectedTcpChannel;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;

public class TcpServerChannel implements Closeable {

    public interface UpdateHandler {
        void onNewChannel(ConnectedNetChannel channel) throws IOException;
        void onNewData(ConnectedNetChannel channel) throws IOException;
    }

    private static final int SELECTOR_TIMEOUT = 500;

    private final SocketAddress mBindAddress;
    private final Logger mLogger;
    private final ClientsStorage mClientsStorage;

    private ServerSocketChannel mBaseChannel;
    private Selector mSelector;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;
        mClientsStorage = new ClientsStorage();

        mBaseChannel = null;
        mSelector = null;
    }

    public void handleUpdates(UpdateHandler handler) throws IOException, TimeoutException {
        openChannel();

        int available = mSelector.select(SELECTOR_TIMEOUT);
        if (available < 1) {
            throw new TimeoutException();
        }

        for (Iterator<SelectionKey> it = mSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            try {
                if (key.isAcceptable()) {
                    // new client
                    SocketChannel channel = mBaseChannel.accept();
                    mLogger.debug("Server received new client from {} at {}",
                            channel.getRemoteAddress(),
                            channel.getLocalAddress());

                    SocketAddress address = channel.getLocalAddress();
                    ConnectedTcpChannel connectedTcpChannel = null;
                    try {
                        connectedTcpChannel = new ConnectedTcpChannel(channel);
                        handler.onNewChannel(connectedTcpChannel);

                        mClientsStorage.putChannel(address, connectedTcpChannel);
                    } catch (IOException e) {
                        // close client
                        mLogger.debug("Error processing new client at {}", address);

                        if (connectedTcpChannel != null) {
                            Closeables.silentClose(connectedTcpChannel);
                        } else {
                            Closeables.silentClose(channel);
                        }

                        throw e;
                    }
                } else if (key.isReadable()) {
                    // new data for client
                    // TODO: STOP READING IF NOT ENOUGH DATA TO READ INSTEAD OF BLOCKING

                    //noinspection resource
                    SocketAddress address = ((SocketChannel) key.channel()).getLocalAddress();
                    try {
                        ConnectedNetChannel channel = mClientsStorage.getChannelByAddress(address);
                        handler.onNewData(channel);
                    } catch (IOException e) {
                        // remove client
                        mLogger.debug("Error processing new data from client at {}", address);
                        ConnectedNetChannel channel = mClientsStorage.remoteByAddress(address);
                        Closeables.silentClose(channel);

                        throw e;
                    }
                }
            } finally {
                it.remove();
            }
        }
    }

    public void writeToAll(ByteBuffer buffer) throws IOException, InterruptedException {
        openChannel();

        Map<SocketAddress, ConnectedNetChannel> clients = mClientsStorage.getAll();
        ThrowableChain chain = Throwables.newChain();
        for (Iterator<Map.Entry<SocketAddress, ConnectedNetChannel>> it = clients.entrySet().iterator();
             it.hasNext();) {
            Map.Entry<SocketAddress, ConnectedNetChannel> entry = it.next();

            try {
                buffer.rewind();
                entry.getValue().write(buffer);

                // remove entries that are okay
                it.remove();
            } catch (IOException e) {
                mLogger.debug("Error sending data to client at {}", entry.getKey());
                chain.chain(e);
            }
        }

        mClientsStorage.removeAll(clients.keySet());
        clients.forEach((k, channel)-> {
            Closeables.silentClose(channel);
        });

        chain.throwIfType(IOException.class);
    }

    @Override
    public void close() throws IOException {
        if (mBaseChannel != null) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
        }

        if (mSelector != null) {
            Closeables.silentClose(mSelector);
            mSelector = null;
        }
    }

    private synchronized void openChannel() throws IOException {
        if (mBaseChannel == null) {
            try {
                mLogger.debug("Opening new TCP SERVER socket in non-blocking mode");

                mSelector = Selector.open();

                mBaseChannel = ServerSocketChannel.open();
                mBaseChannel.configureBlocking(false);

                mLogger.debug("Binding TCP SERVER to {}", mBindAddress);
                mBaseChannel.bind(mBindAddress);

                mBaseChannel.register(mSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }
}
