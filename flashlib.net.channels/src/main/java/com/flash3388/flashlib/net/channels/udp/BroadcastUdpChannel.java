package com.flash3388.flashlib.net.channels.udp;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.MembershipKey;

public class BroadcastUdpChannel extends UdpChannel {

    public BroadcastUdpChannel(InetAddress broadcastAddress,
                               int remotePort,
                               SocketAddress bindAddress,
                               Logger logger,
                               Runnable onOpen) {
        super(bindAddress, logger, onOpen, (channel)-> {
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
            return ()-> {};
        });

        setRemoteAddress(new InetSocketAddress(broadcastAddress, remotePort));
    }
}
