package com.flash3388.flashlib.net.channels.udp;

import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;

public class MulticastUdpChannel extends UdpChannel {

    public MulticastUdpChannel(NetworkInterface multicastInterface,
                               InetAddress multicastGroup,
                               int remotePort,
                               SocketAddress bindAddress,
                               Logger logger,
                               Runnable onOpen) {
        super(bindAddress, logger, onOpen, (channel)-> {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, true);
            channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, 3);
            channel.join(multicastGroup, multicastInterface);
            return ()->{};
        });

        setRemoteAddress(new InetSocketAddress(multicastGroup, remotePort));
    }
}
