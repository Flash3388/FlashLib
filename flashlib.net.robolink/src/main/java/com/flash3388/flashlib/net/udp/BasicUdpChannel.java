package com.flash3388.flashlib.net.udp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BasicUdpChannel implements Closeable {

    private final int mBindPort;
    private final int mReadTimeout;
    private final Logger mLogger;
    private final AtomicReference<DatagramChannel> mChannel;
    private final Lock mChannelLock;

    public BasicUdpChannel(int bindPort, Time readTimeout, Logger logger) {
        mBindPort = bindPort;
        mReadTimeout = (int) readTimeout.valueAsMillis();
        mLogger = logger;
        mChannel = new AtomicReference<>();
        mChannelLock = new ReentrantLock();
    }

    public void writeTo(ByteBuffer buffer, SocketAddress address) throws IOException {
        try {
            //noinspection resource
            DatagramChannel channel = openChannel();

            mLogger.debug("Writing packet to {}", address);
            channel.send(buffer, address);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public void broadcast(ByteBuffer buffer) throws IOException {
        InetAddress address = InetAddress.getByName("255.255.255.255");
        SocketAddress socketAddress = new InetSocketAddress(address, mBindPort);
        writeTo(buffer, socketAddress);
    }

    public SocketAddress read(ByteBuffer buffer) throws IOException, TimeoutException {
        try {
            //noinspection resource
            DatagramChannel channel = openChannel();
            SocketAddress address = channel.receive(buffer);
            if (address != null) {
                mLogger.debug("Received packet from {}", address);
            }
            return address;
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        mChannelLock.lock();
        try {
            DatagramChannel channel = mChannel.getAndSet(null);
            Closeables.silentClose(channel);
        } finally {
            mChannelLock.unlock();
        }
    }

    private DatagramChannel openChannel() throws IOException {
        DatagramChannel channel = mChannel.get();
        if (channel == null) {
            mChannelLock.lock();
            try {
                channel = mChannel.get();
                if (channel != null) {
                    return channel;
                }

                mLogger.debug("Opening new UDP socket in non-blocking mode");

                channel = DatagramChannel.open();
                channel.configureBlocking(true);
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
                channel.socket().setSoTimeout(mReadTimeout);

                SocketAddress address = new InetSocketAddress(mBindPort);
                channel.bind(address);
                mLogger.debug("UDP socket bound at {}", address);

                mChannel.set(channel);
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
