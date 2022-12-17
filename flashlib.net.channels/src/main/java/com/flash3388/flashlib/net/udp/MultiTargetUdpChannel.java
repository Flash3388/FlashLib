package com.flash3388.flashlib.net.udp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
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

public class MultiTargetUdpChannel implements Closeable {

    private final int[] mBindPorts;
    private final Logger mLogger;
    private final AtomicReference<UnconnectedUdpChannel> mChannel;
    private final Lock mChannelLock;

    public MultiTargetUdpChannel(int[] bindPorts, Logger logger) {
        mBindPorts = bindPorts;
        mLogger = logger;
        mChannel = new AtomicReference<>();
        mChannelLock = new ReentrantLock();
    }

    public void writeTo(ByteBuffer buffer, SocketAddress address) throws IOException {
        try {
            //noinspection resource
            UnconnectedUdpChannel channel = openChannel();
            channel.writeTo(buffer, address);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public SocketAddress read(ByteBuffer buffer) throws IOException, TimeoutException {
        try {
            //noinspection resource
            UnconnectedUdpChannel channel = openChannel();
            return channel.read(buffer);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        mChannelLock.lock();
        try {
            UnconnectedUdpChannel channel = mChannel.getAndSet(null);
            Closeables.silentClose(channel);
        } finally {
            mChannelLock.unlock();
        }
    }

    private UnconnectedUdpChannel openChannel() throws IOException {
        UnconnectedUdpChannel channel = mChannel.get();
        if (channel == null) {
            DatagramChannel baseChannel = null;

            mChannelLock.lock();
            try {
                channel = mChannel.get();
                if (channel != null) {
                    return channel;
                }

                mLogger.debug("Opening new UDP socket in non-blocking mode");

                baseChannel = DatagramChannel.open();
                baseChannel.configureBlocking(false);
                baseChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                baseChannel.setOption(StandardSocketOptions.SO_BROADCAST, true);

                bind(baseChannel);

                channel = new UnconnectedUdpChannel(baseChannel);
                mChannel.set(channel);
            } catch (IOException e) {
                if (baseChannel != null) {
                    Closeables.silentClose(baseChannel);
                }
                throw e;
            } finally {
                mChannelLock.unlock();
            }
        }

        return channel;
    }

    private void bind(DatagramChannel channel) throws IOException {
        boolean isBound = false;
        ThrowableChain chain = Throwables.newChain();
        for (int port : mBindPorts) {
            try {
                SocketAddress address = new InetSocketAddress(port);
                channel.bind(address);
                isBound = true;

                mLogger.debug("UDP socket bound at {}", address);
                break;
            } catch (IOException e) {
                chain.chain(e);
            }
        }

        if (!isBound) {
            chain.throwIfType(IOException.class);
            throw new IOException("not bound");
        }
    }
}
