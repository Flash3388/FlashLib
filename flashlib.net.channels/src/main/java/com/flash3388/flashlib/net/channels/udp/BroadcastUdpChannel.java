package com.flash3388.flashlib.net.channels.udp;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.StandardSocketOptions;
import java.nio.channels.MembershipKey;

public class BroadcastUdpChannel extends UdpChannel {

    public BroadcastUdpChannel(int remotePort,
                               int bindPort, Logger logger, Runnable onOpen) {
        super(bindPort, logger, onOpen, (channel)-> {
            channel.setOption(StandardSocketOptions.SO_BROADCAST, true);
            return ()-> {};
        });

        try {
            InetAddress address = InetAddress.getByName("255.255.255.255");
            setRemoteAddress(new InetSocketAddress(address, remotePort));
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}