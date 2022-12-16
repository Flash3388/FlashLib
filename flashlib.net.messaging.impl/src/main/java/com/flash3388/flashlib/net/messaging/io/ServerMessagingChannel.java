package com.flash3388.flashlib.net.messaging.io;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.closeables.Closer;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ServerMessagingChannel implements MessagingChannel, ServerChannel {

    private static final int READ_TIMEOUT = 500;
    private static final int ACCEPT_TIMEOUT = 500;

    private final SocketAddress mBindAddress;
    private final MessageSerializer mSerializer;
    private final Map<Channel, TcpSocketChannel> mClients;

    private final Lock mServerLock;

    private ServerSocketChannel mServer;
    private Selector mServerSelector;
    private final AtomicReference<Selector> mClientSelector;

    public ServerMessagingChannel(SocketAddress bindAddress, KnownMessageTypes messageTypes) {
        mBindAddress = bindAddress;
        mSerializer = new MessageSerializer(messageTypes);
        mClients = new ConcurrentHashMap<>();

        mServerLock = new ReentrantLock();

        mServer = null;
        mServerSelector = null;
        mClientSelector = new AtomicReference<>(null);
    }

    @Override
    public void handleNewConnections() throws IOException, InterruptedException {
        initializeServer();
        int available = mServerSelector.select(ACCEPT_TIMEOUT);
        if (available < 1) {
            throw new InterruptedException();
        }

        for (Iterator<SelectionKey> it = mServerSelector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            try {
                if (key.isAcceptable()) {
                    SocketChannel channel = mServer.accept();
                    handleNewConnection(channel);
                }
            } finally {
                it.remove();
            }
        }
    }

    @Override
    public boolean establishConnection() throws IOException, TimeoutException {
        return false;
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException {
        ThrowableChain chain = Throwables.newChain();
        for (Iterator<? extends TcpChannel> it = mClients.values().iterator(); it.hasNext();) {
            TcpChannel channel = it.next();
            try (ChannelOutput output = channel.output();
                 DataOutputStream dataOutputStream = new DataOutputStream(output)) {
                mSerializer.write(dataOutputStream, message);
            } catch (IOException e) {
                it.remove();
                channel.closeChannel();
                chain.chain(e);
            }
        }

        chain.throwIfType(IOException.class);
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        Selector selector = mClientSelector.get();
        if (selector == null) {
            throw new IOException("server not initialized");
        }

        int available = selector.select(READ_TIMEOUT);
        if (available < 1) {
            throw new TimeoutException();
        }

        for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext();) {
            SelectionKey key = it.next();

            TcpChannel channel = mClients.get(key.channel());
            if (channel != null) {
                try {
                    return readFromChannel(channel);
                } catch (IOException e) {
                    mClients.remove(channel.getChannel());
                    channel.closeChannel();
                } catch (TimeoutException e) {
                    // ignore this and move on
                }
            }

            it.remove();
        }

        throw new TimeoutException();
    }

    private void initializeServer() {
        mServerLock.lock();
        try {
            createServerSocket();
        } catch (IOException e) {
            closeServerSocket();
        } finally {
            mServerLock.unlock();
        }
    }

    private void createServerSocket() throws IOException {
        mServer = ServerSocketChannel.open();
        mServer.bind(mBindAddress);
        mServer.configureBlocking(true);

        mServerSelector = Selector.open();
        mServer.register(mServerSelector, SelectionKey.OP_ACCEPT);

        mClientSelector.set(Selector.open());
    }

    private void closeServerSocket() {
        Closeables.silentClose(Closer.with(
                mClientSelector.getAndSet(null),
                mServerSelector,
                mServer));
        mServer = null;
        mServerSelector = null;
    }

    private void handleNewConnection(SocketChannel channel) throws IOException {
        mServerLock.lock();
        try {
            channel.register(mClientSelector.get(), SelectionKey.OP_READ);
            mClients.put(channel, new TcpSocketChannel(channel));
        } finally {
            mServerLock.unlock();
        }
    }

    private Message readFromChannel(TcpChannel channel) throws IOException, TimeoutException {
        DataInput dataInput = channel.input();
        try  {
            return mSerializer.read(dataInput);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException();
        }
    }

    @Override
    public void close() throws IOException {
        mServerLock.lock();
        try {
            mClients.forEach((k, v)-> v.closeChannel());
            mClients.clear();

            try {
                Closer closer = Closer.empty();
                if (mServer != null) {
                    closer.add(mServer);
                }
                if (mServerSelector != null) {
                    closer.add(mServerSelector);
                }
                if (mClientSelector.get() != null) {
                    closer.add(mClientSelector.get());
                }

                closer.close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        } finally {
            mServerLock.unlock();
        }
    }
}
