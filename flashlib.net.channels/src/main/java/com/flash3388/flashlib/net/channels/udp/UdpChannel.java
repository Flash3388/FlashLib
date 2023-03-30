package com.flash3388.flashlib.net.channels.udp;

import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingConsumer;
import com.castle.util.function.ThrowingFunction;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UdpChannel implements NetChannel {

    private final int mBindPort;
    protected final Logger mLogger;
    private final Runnable mOnOpen;
    private final ThrowingFunction<DatagramChannel, Closeable, IOException> mConfigureChannel;

    private final AtomicReference<DatagramChannel> mUnderlyingChannel;
    private final AtomicReference<SocketAddress> mRemoteAddress;
    private Closeable mCustomCloseable;
    private final Lock mChannelLock;

    public UdpChannel(SocketAddress remote, int bindPort, Logger logger, Runnable onOpen,
                      ThrowingFunction<DatagramChannel, Closeable, IOException> configureChannel) {
        mBindPort = bindPort;
        mLogger = logger;
        mOnOpen = onOpen;
        mConfigureChannel = configureChannel;

        mUnderlyingChannel = new AtomicReference<>();
        mRemoteAddress = new AtomicReference<>(remote);
        mCustomCloseable = null;
        mChannelLock = new ReentrantLock();
    }

    public UdpChannel(SocketAddress remote, int bindPort, Logger logger, Runnable onOpen) {
        this(remote, bindPort, logger, onOpen, null);
    }

    public UdpChannel(int bindPort, Logger logger, Runnable onOpen) {
        this(null, bindPort, logger, onOpen);
    }

    public UdpChannel(int bindPort, Logger logger, Runnable onOpen,
                      ThrowingFunction<DatagramChannel, Closeable, IOException> configureChannel) {
        this(null, bindPort, logger, onOpen, configureChannel);
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        if (remoteAddress == null || remoteAddress.equals(mRemoteAddress.get())) {
            return;
        }

        mLogger.debug("Configuring remote address to {}", remoteAddress);
        mRemoteAddress.set(remoteAddress);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        try {
            //noinspection resource
            DatagramChannel channel = openChannel();
            SocketAddress remote = channel.receive(buffer);
            if (remote == null) {
                return new IncomingData(null, 0);
            }
            if (remote.equals(channel.getLocalAddress())) {
                mLogger.debug("Received data from self");
                return new IncomingData(null, 0);
            }

            mLogger.debug("Received data from {}", remote);
            return new IncomingData(remote, buffer.position());
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        SocketAddress remote = mRemoteAddress.get();
        if (remote == null) {
            mLogger.debug("Remote address not configured");
            return;
        }

        try {
            mLogger.debug("Sending data to {}", remote);

            //noinspection resource
            DatagramChannel channel = openChannel();
            channel.send(buffer, remote);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        mChannelLock.lock();
        try {
            DatagramChannel channel = mUnderlyingChannel.getAndSet(null);
            if (mCustomCloseable != null) {
                Closeables.silentClose(mCustomCloseable);
            }
            Closeables.silentClose(channel);
        } finally {
            mChannelLock.unlock();
        }
    }

    private DatagramChannel openChannel() throws IOException {
        DatagramChannel channel = mUnderlyingChannel.get();
        if (channel == null) {
            mChannelLock.lock();
            try {
                channel = mUnderlyingChannel.get();
                if (channel != null) {
                    return channel;
                }

                mLogger.debug("Opening new UDP socket in non-blocking mode");

                channel = DatagramChannel.open();
                channel.configureBlocking(false);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                if (mConfigureChannel != null) {
                    mCustomCloseable = mConfigureChannel.apply(channel);
                }

                SocketAddress address = new InetSocketAddress(mBindPort);
                channel.bind(address);
                mLogger.debug("UDP socket bound at {}", address);

                mOnOpen.run();
                mUnderlyingChannel.set(channel);
            } catch (IOException e) {
                if (channel != null) {
                    Closeables.silentClose(channel);
                }
                throw e;
            } finally {
                mChannelLock.unlock();
            }
        }

        return channel;
    }
}
