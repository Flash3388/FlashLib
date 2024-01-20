package com.flash3388.flashlib.net.channels.udp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingConsumer;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

public class UdpConnector implements NetChannelConnector {
    // TODO: SUPPORT FOR AUTO-REPLYING CHANNEL

    private final SocketAddress mBindAddress;
    private final ThrowingConsumer<DatagramChannel, IOException> mConfigureChannel;
    private final Logger mLogger;

    public UdpConnector(SocketAddress bindAddress,
                        ThrowingConsumer<DatagramChannel, IOException> configureChannel,
                        Logger logger) {
        mBindAddress = bindAddress;
        mConfigureChannel = configureChannel;
        mLogger = logger;
    }

    public UdpConnector(SocketAddress bindAddress, Logger logger) {
        this(bindAddress, null, logger);
    }

    public static UdpConnector multicast(NetworkInterface multicastInterface,
                                         InetAddress multicastGroup,
                                         SocketAddress bindAddress,
                                         Logger logger) {
        return new UdpConnector(bindAddress, (channel)-> {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 3);
            channel.join(multicastGroup, multicastInterface);
        }, logger);
    }

    public static UdpConnector broadcast(SocketAddress bindAddress,
                                         Logger logger) {
        return new UdpConnector(bindAddress, (channel)-> {
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
        }, logger);
    }

    @Override
    public NetChannel connect(SocketAddress remote, Time timeout) throws IOException, TimeoutException, InterruptedException {
        DatagramChannel channel = openChannel();
        return new UdpChannel(channel, remote);
    }

    @Override
    public void close() throws IOException {

    }

    private DatagramChannel openChannel() throws IOException {
        DatagramChannel channel = null;
        try {
            mLogger.debug("Opening new UDP socket in non-blocking mode");

            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);

            if (mConfigureChannel != null) {
                mConfigureChannel.accept(channel);
            }

            channel.bind(mBindAddress);
            mLogger.debug("UDP socket bound at {}", mBindAddress);
        } catch (IOException e) {
            if (channel != null) {
                Closeables.silentClose(channel);
            }

            throw e;
        }

        return channel;
    }
}
