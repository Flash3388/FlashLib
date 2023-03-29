package com.flash3388.flashlib.net.channels;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AutoConnectingChannel implements NetChannel {

    private static final Time CONNECTION_WAIT_TIME = Time.milliseconds(100);

    private final NetChannelConnector mConnector;
    private final SocketAddress mRemoteAddress;
    private final Logger mLogger;
    private final Runnable mOnConnection;

    private final AtomicReference<NetChannel> mUnderlyingChannel;
    private final Lock mLock;

    public AutoConnectingChannel(NetChannelConnector connector, SocketAddress remoteAddress, Logger logger, Runnable onConnection) {
        mConnector = connector;
        mRemoteAddress = remoteAddress;
        mLogger = logger;
        mOnConnection = onConnection;

        mUnderlyingChannel = new AtomicReference<>();
        mLock = new ReentrantLock();
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        NetChannel channel = getChannel();
        return channel.read(buffer);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        NetChannel channel = getChannel();
        channel.write(buffer);
    }

    @Override
    public void close() throws IOException {
        mLock.lock();
        try {
            NetChannel channel = mUnderlyingChannel.getAndSet(null);
            Closeables.silentClose(channel);
            Closeables.silentClose(mConnector);
        } finally {
            mLock.unlock();
        }
    }

    private NetChannel getChannel() throws IOException {
        mLock.lock();
        try {
            NetChannel channel = mUnderlyingChannel.get();
            if (channel != null) {
                return channel;
            }

            channel = doConnection();
            if (channel == null) {
                throw new ClosedChannelException();
            }

            mOnConnection.run();
            mUnderlyingChannel.set(channel);

            return channel;
        } finally {
            mLock.unlock();
        }
    }

    private NetChannel doConnection() {
        while (true) {
            try {
                mLogger.debug("Attempting to connect to {}", mRemoteAddress);
                return mConnector.connect(mRemoteAddress, CONNECTION_WAIT_TIME);
            } catch (IOException e) {
                mLogger.error("Encountered error while connecting", e);
                // oh well, try again
            } catch (TimeoutException e) {
                // oh well, try again
            } catch (InterruptedException e) {
                mLogger.warn("Current thread interrupted");
                Thread.currentThread().interrupt();
                return null;
            }
        }
    }
}
