package com.flash3388.flashlib.net.channels.udp;

import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.channels.MembershipKey;

public class MulticastUdpChannel extends UdpChannel {

    public MulticastUdpChannel(NetworkInterface multicastInterface,
                               InetAddress multicastGroup,
                               int remotePort,
                               int bindPort, Logger logger, Runnable onOpen) {
        super(bindPort, logger, onOpen, (channel)-> {
            channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, multicastInterface);
            MembershipKey key = channel.join(multicastGroup, multicastInterface);
            return key::drop;
        });

        setRemoteAddress(new InetSocketAddress(multicastGroup, remotePort));
    }
}
