package com.flash3388.flashlib.net.channels.udp;

import com.castle.util.function.ThrowingConsumer;
import com.flash3388.flashlib.net.channels.IpNetAddress;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.RemoteConfigurableChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;

public class UdpChannelOpener implements NetChannelOpener<RemoteConfigurableChannel> {

    private final SocketAddress mBindAddress;
    private final SocketAddress mRemoteAddress;
    private final ThrowingConsumer<DatagramChannel, IOException> mConfigureChannel;
    private final Logger mLogger;

    public UdpChannelOpener(SocketAddress bindAddress,
                            SocketAddress remoteAddress,
                            ThrowingConsumer<DatagramChannel, IOException> configureChannel,
                            Logger logger) {
        mBindAddress = bindAddress;
        mRemoteAddress = remoteAddress;
        mConfigureChannel = configureChannel;
        mLogger = logger;
    }

    public static UdpChannelOpener targeted(SocketAddress bindAddress,
                                            SocketAddress remoteAddress,
                                            Logger logger) {
        return new UdpChannelOpener(bindAddress, remoteAddress, null, logger);
    }

    public static UdpChannelOpener multicast(NetworkInterface multicastInterface,
                                             InetAddress multicastGroup,
                                             int remotePort,
                                             SocketAddress bindAddress,
                                             Logger logger) {
        return new UdpChannelOpener(bindAddress, new InetSocketAddress(multicastGroup, remotePort),
                (channel)-> {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 3);
            channel.join(multicastGroup, multicastInterface);
        }, logger);
    }

    public static UdpChannelOpener broadcast(SocketAddress bindAddress,
                                             int remotePort,
                                             Logger logger) {
        return new UdpChannelOpener(bindAddress, new InetSocketAddress("255.255.255.255", remotePort),
                (channel)-> {
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
        }, logger);
    }

    @Override
    public boolean isTargetChannelStreaming() {
        return false;
    }

    @Override
    public RemoteConfigurableChannel open() throws IOException {
        UdpChannel channel = new UdpChannel(mBindAddress, mConfigureChannel, mLogger);
        channel.setRemote(new IpNetAddress(mRemoteAddress));

        return channel;
    }
}
