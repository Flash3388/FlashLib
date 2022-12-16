package com.flash3388.flashlib.net.messaging.impl;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingFunction;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TcpServerChannel implements Closeable {

    private static final int ACCEPT_TIMEOUT = 500;
    private static final int READ_TIMEOUT = 100;

    private final SocketAddress mBindAddress;
    private final Map<Channel, TcpSocketChannel> mClients;

    private Selector mAcceptSelector;
    private Selector mReadSelector;
    private ServerSocketChannel mServer;

    public TcpServerChannel(SocketAddress bindAddress) {
        mBindAddress = bindAddress;
        mClients = new ConcurrentHashMap<>();
    }

    public void acceptNewClient() throws IOException, TimeoutException {
        openServer();

        int available = mAcceptSelector.select(ACCEPT_TIMEOUT);
        if (available < 1) {
            throw new TimeoutException();
        }

        for (Iterator<SelectionKey> it = mAcceptSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            try {
                if (key.isAcceptable()) {
                    SocketChannel channel = mServer.accept();
                    channel.register(mReadSelector, SelectionKey.OP_READ);

                    mClients.put(channel, new TcpSocketChannel(channel));
                }
            } finally {
                it.remove();
            }
        }
    }

    public void writeToAll(ByteBuffer buffer) throws IOException {
        ThrowableChain chain = Throwables.newChain();
        for (Iterator<? extends TcpSocketChannel> it = mClients.values().iterator(); it.hasNext();) {
            TcpSocketChannel channel = it.next();
            try {
                channel.write(buffer);
            } catch (IOException e) {
                it.remove();
                chain.chain(e);
            }
        }

        chain.throwIfType(IOException.class);
    }

    public <T> T waitForReadAvailable(ThrowingFunction<BufferedReader, T, IOException> func) throws IOException, TimeoutException {
        openServer();

        int available = mReadSelector.select(READ_TIMEOUT);
        if (available < 1) {
            throw new TimeoutException();
        }

        for (Iterator<SelectionKey> it = mReadSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            TcpSocketChannel channel = mClients.get(key.channel());
            if (channel != null) {
                try {
                    return func.apply(channel.reader());
                } catch (IOException e) {
                    channel.close();
                    mClients.remove(key.channel());
                    throw e;
                }
            }

            it.remove();
        }

        throw new IOException();
    }

    private synchronized void openServer() throws IOException {
        if (mServer == null) {
            try {
                mAcceptSelector = Selector.open();
                mReadSelector = Selector.open();

                mServer = ServerSocketChannel.open();
                mServer.configureBlocking(false);
                mServer.bind(mBindAddress);

                mServer.register(mAcceptSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                close();
                throw e;
            }
        }
    }

    @Override
    public synchronized void close() {
        mClients.forEach((k, v)-> {
            v.close();
        });
        mClients.clear();

        if (mServer != null) {
            Closeables.silentClose(mServer);
            mServer = null;
        }

        if (mAcceptSelector != null) {
            Closeables.silentClose(mAcceptSelector);
            mAcceptSelector = null;
        }

        if (mReadSelector != null) {
            Closeables.silentClose(mReadSelector);
            mReadSelector = null;
        }
    }
}
