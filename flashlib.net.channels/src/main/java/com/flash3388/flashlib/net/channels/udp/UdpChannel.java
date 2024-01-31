package com.flash3388.flashlib.net.channels.udp;

import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingConsumer;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;

public class UdpChannel implements NetChannel {

    private final SocketAddress mRemoteAddress;
    private final Logger mLogger;
    private final DatagramChannel mChannel;

    public UdpChannel(SocketAddress remoteAddress,
                      SocketAddress bindAddress,
                      ThrowingConsumer<DatagramChannel, IOException> configureChannel,
                      Logger logger) throws IOException {
        mRemoteAddress = remoteAddress;
        mLogger = logger;
        mChannel = openChannel(bindAddress, configureChannel);
    }

    @Override
    public UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException {
        return updater.register(mChannel, SelectionKey.OP_READ | SelectionKey.OP_WRITE, listener);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        SocketAddress remote = mChannel.receive(buffer);
        if (remote == null) {
            return new IncomingData(null, 0);
        }
        if (remote.equals(mChannel.getLocalAddress())) {
            return new IncomingData(null, 0);
        }

        return new IncomingData(remote, buffer.position());
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        mChannel.send(buffer, mRemoteAddress);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }

    private DatagramChannel openChannel(SocketAddress bindAddress,
                                        ThrowingConsumer<DatagramChannel, IOException> configureChannel) throws IOException {
        DatagramChannel channel = null;
        try {
            mLogger.debug("Opening new UDP socket in non-blocking mode");

            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            if (configureChannel != null) {
                configureChannel.accept(channel);
            }

            channel.bind(bindAddress);
            mLogger.debug("UDP socket bound at {}", bindAddress);
        } catch (IOException e) {
            if (channel != null) {
                Closeables.silentClose(channel);
            }

            throw e;
        }

        return channel;
    }
}
