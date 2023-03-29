package com.flash3388.flashlib.net.channels.udp;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import org.slf4j.Logger;

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
    private final Logger mLogger;
    private final Runnable mOnOpen;

    private final AtomicReference<DatagramChannel> mUnderlyingChannel;
    private final AtomicReference<SocketAddress> mRemoteAddress;
    private final Lock mChannelLock;

    public UdpChannel(SocketAddress remote, int bindPort, Logger logger, Runnable onOpen) {
        mBindPort = bindPort;
        mLogger = logger;
        mOnOpen = onOpen;

        mUnderlyingChannel = new AtomicReference<>();
        mRemoteAddress = new AtomicReference<>(remote);
        mChannelLock = new ReentrantLock();
    }

    public UdpChannel(int bindPort, Logger logger, Runnable onOpen) {
        this(null, bindPort, logger, onOpen);
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        mRemoteAddress.set(remoteAddress);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        try {
            //noinspection resource
            DatagramChannel channel = openChannel();
            SocketAddress remote = channel.receive(buffer);
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
