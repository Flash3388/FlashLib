package com.flash3388.flashlib.net.impl;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpServerChannel implements Closeable {

    public interface UpdateHandler {

        void onNewClientData(ReadableChannel channel) throws IOException;
        void onNewChannel(int identifier) throws IOException;
    }

    private static final int SELECTOR_TIMEOUT = 500;

    private final SocketAddress mBindAddress;
    private final Logger mLogger;
    private final Map<Channel, TcpSocketChannel> mClients;
    private final Map<Integer, Channel> mClientIdentifiers;

    private final Lock mClientsChangeLock;
    private final Condition mHasClients;
    private final AtomicInteger mClientIdentifier;

    private Selector mSelector;
    private Selector mReadSelector;
    private ServerSocketChannel mServer;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;
        mClients = new HashMap<>(5);
        mClientIdentifiers = new HashMap<>(5);

        mClientsChangeLock = new ReentrantLock();
        mHasClients = mClientsChangeLock.newCondition();
        mClientIdentifier = new AtomicInteger(0);

        mSelector = null;
        mServer = null;
    }

    public void handleUpdates(UpdateHandler handler) throws IOException, TimeoutException {
        openServer();

        int available = mSelector.select(SELECTOR_TIMEOUT);
        if (available < 1) {
            throw new TimeoutException();
        }

        for (Iterator<SelectionKey> it = mSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            try {
                if (key.isAcceptable()) {
                    // new client
                    SocketChannel channel = mServer.accept();
                    int identifier = mClientIdentifier.incrementAndGet();

                    mClientsChangeLock.lock();
                    try {
                        mLogger.debug("Server received new client, id {}", identifier);

                        channel.configureBlocking(false);
                        channel.register(mSelector, SelectionKey.OP_READ);

                        handler.onNewChannel(identifier);

                        mClients.put(channel, new TcpSocketChannel(channel, identifier));
                        mClientIdentifiers.put(identifier, channel);

                        mHasClients.signalAll();
                    } catch (IOException e) {
                        mLogger.debug("Server encountered error while processing new client {}", identifier);
                        Closeables.silentClose(channel);
                        removeClient(channel);

                        throw e;
                    } finally {
                        mClientsChangeLock.unlock();
                    }
                } else if (key.isReadable()) {
                    // client ready to read

                    TcpSocketChannel channel;
                    mClientsChangeLock.lock();
                    try {
                        channel = mClients.get(key.channel());
                    } finally {
                        mClientsChangeLock.unlock();
                    }

                    if (channel != null) {
                        try {
                            mLogger.debug("Server received data from client, id {}", channel.getIdentifier());
                            handler.onNewClientData(channel);
                        } catch (IOException e) {
                            mLogger.debug("Server encountered error processing data from client, id {}",
                                    channel.getIdentifier());
                            removeClient(key.channel());
                            throw e;
                        }
                    }
                }
            } finally {
                it.remove();
            }
        }
    }

    public void waitUntilHasClients() throws InterruptedException {
        mClientsChangeLock.lock();
        try {
            while (mClients.size() < 1) {
                mHasClients.await();
            }
        } finally {
            mClientsChangeLock.unlock();
        }
    }

    public void writeToAllBut(ByteBuffer buffer, int identifier) throws IOException {
        List<TcpSocketChannel> clients;
        mClientsChangeLock.lock();
        try {
            clients = new LinkedList<>(mClients.values());
        } finally {
            mClientsChangeLock.unlock();
        }

        ThrowableChain chain = Throwables.newChain();
        // remove channels with no errors
        for (Iterator<? extends TcpSocketChannel> it = clients.iterator(); it.hasNext();) {
            TcpSocketChannel channel = it.next();
            if (channel.getIdentifier() == identifier) {
                it.remove();
                continue;
            }

            try {
                writeToChannel(channel, buffer);
                it.remove();
            } catch (IOException e) {
                chain.chain(e);
            }
        }

        mClientsChangeLock.lock();
        try {
            clients.forEach((v)-> removeClientById(v.getIdentifier()));
        } finally {
            mClientsChangeLock.unlock();
        }

        chain.throwIfType(IOException.class);
    }

    public void writeToAll(ByteBuffer buffer) throws IOException {
        writeToAllBut(buffer, -1);
    }

    private void writeToChannel(TcpSocketChannel channel, ByteBuffer buffer) throws IOException {
        mLogger.debug("Server writing to channel {}", channel.getIdentifier());
        try {
            buffer.rewind();
            channel.write(buffer);
        } catch (IOException e) {
            mLogger.debug("Server failed writing to channel {}, closing it", channel.getIdentifier());
            channel.close();
            throw e;
        }
    }

    private void removeClient(Object key) {
        mClientsChangeLock.lock();
        try {
            //noinspection SuspiciousMethodCalls
            TcpSocketChannel channel = mClients.remove(key);
            if (channel != null) {
                mLogger.debug("Server removing client, id {}", channel.getIdentifier());
                //noinspection resource
                mClientIdentifiers.remove(channel.getIdentifier());
                channel.close();
            }
        } finally {
            mClientsChangeLock.unlock();
        }
    }

    private void removeClientById(int key) {
        mClientsChangeLock.lock();
        try {
            Channel baseChannel = mClientIdentifiers.remove(key);
            if (baseChannel == null) {
                return;
            }

            TcpSocketChannel channel = mClients.remove(baseChannel);
            if (channel != null) {
                mLogger.debug("Server removing client, id {}", channel.getIdentifier());
                channel.close();
            }
        } finally {
            mClientsChangeLock.unlock();
        }
    }

    private synchronized void openServer() throws IOException {
        if (mServer == null) {
            try {
                mLogger.debug("Server opening socket in non-blocking mode");

                mSelector = Selector.open();

                mServer = ServerSocketChannel.open();
                mServer.configureBlocking(false);

                mLogger.debug("Server binding socket to {}", mBindAddress);
                mServer.bind(mBindAddress);

                mServer.register(mSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }

    @Override
    public synchronized void close() {
        mLogger.debug("Server closing socket and connections");

        mClients.forEach((k, v)-> {
            v.close();
        });
        mClients.clear();

        if (mReadSelector != null) {
            Closeables.silentClose(mReadSelector);
            mReadSelector = null;
        }

        if (mServer != null) {
            Closeables.silentClose(mServer);
            mServer = null;
        }

        if (mSelector != null) {
            mSelector.wakeup();
            Closeables.silentClose(mSelector);
            mSelector = null;
        }
    }
}
