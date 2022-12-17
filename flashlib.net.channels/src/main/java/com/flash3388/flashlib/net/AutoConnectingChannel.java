package com.flash3388.flashlib.net;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoConnectingChannel implements ConnectedNetChannel {

    private static final Time CONNECTION_WAIT_TIME = Time.milliseconds(100);

    private final NetConnector mConnector;
    private final SocketAddress mRemoteAddress;
    private final Logger mLogger;

    private final Lock mConnectionLock;
    private final Condition mConnected;
    private final AtomicBoolean mConnectorThread;
    private final AtomicReference<Runnable> mOnConnectionCallback;
    private final AtomicReference<ConnectedNetChannel> mUnderlyingChannel;

    public AutoConnectingChannel(NetConnector connector, SocketAddress remoteAddress, Logger logger) {
        mConnector = connector;
        mRemoteAddress = remoteAddress;
        mLogger = logger;

        mConnectionLock = new ReentrantLock();
        mConnected = mConnectionLock.newCondition();
        mConnectorThread = new AtomicBoolean(false);
        mOnConnectionCallback = new AtomicReference<>(null);
        mUnderlyingChannel = new AtomicReference<>(null);
    }

    public void setOnConnection(Runnable callback) {
        mOnConnectionCallback.set(callback);
    }

    public void waitForConnection() throws InterruptedException, IOException {
        getChannel();
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException, InterruptedException {
        try {
            ConnectedNetChannel channel = getChannel();
            channel.write(buffer);
        } catch (IOException e) {
            closeChannel();
            throw e;
        }
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException, InterruptedException, TimeoutException {
        try {
            ConnectedNetChannel channel = getChannel();
            return channel.read(buffer);
        } catch (IOException e) {
            closeChannel();
            throw e;
        }
    }

    @Override
    public void close() {
        ConnectedNetChannel channel = mUnderlyingChannel.getAndSet(null);
        Closeables.silentClose(channel);
        Closeables.silentClose(mConnector);

        mConnectorThread.set(false);
    }

    private ConnectedNetChannel getChannel() throws IOException, InterruptedException {
        mConnectionLock.lock();
        try {
            if (mUnderlyingChannel.get() == null) {
                if (!mConnectorThread.getAndSet(true)) {
                    try {
                        doConnection();
                    } catch (InterruptedException e) {
                        closeChannel();
                        throw e;
                    }
                } else {
                    mConnected.await();
                }
            }

            ConnectedNetChannel channel = mUnderlyingChannel.get();
            if (channel == null) {
                throw new ClosedChannelException();
            }

            return channel;
        } finally {
            mConnectionLock.unlock();
        }
    }

    private void doConnection() throws InterruptedException {
        while (true) {
            ConnectedNetChannel underlyingChannel = null;
            try {
                mLogger.debug("Attempting to connect to {}", mRemoteAddress);
                underlyingChannel = mConnector.connect(mRemoteAddress, CONNECTION_WAIT_TIME);

                Runnable runnable = mOnConnectionCallback.get();
                if (runnable != null) {
                    try {
                        runnable.run();
                    } catch (Throwable t) {
                        // catch user errors
                    }
                }

                mUnderlyingChannel.set(underlyingChannel);

                mConnected.signalAll();

                return;
            } catch (IOException e) {
                mLogger.debug("Encountered error while connecting", e);
                // oh well, try again
            } catch (TimeoutException e) {
                // oh well, try again
            }
        }
    }

    private void closeChannel() {
        ConnectedNetChannel channel = mUnderlyingChannel.getAndSet(null);
        Closeables.silentClose(channel);
        mConnectorThread.set(false);

        mConnectionLock.lock();
        try {
            mConnected.signalAll();
        } finally {
            mConnectionLock.unlock();
        }
    }
}
