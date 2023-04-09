package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.net.hfcs.impl.HfcsServiceBase;
import com.flash3388.flashlib.net.hfcs.impl.HfcsServices;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class HfcsConfiguration {

    interface Creator {
        HfcsServiceBase create(InstanceId ourId, Clock clock) throws Exception;
    }

    final HfcsConfiguration.Creator creator;

    private HfcsConfiguration(HfcsConfiguration.Creator creator) {
        this.creator = creator;
    }

    public static HfcsConfiguration disabled() {
        return new HfcsConfiguration(null);
    }

    public static HfcsConfiguration replyToSenderMode(int bindPort) {
        return new HfcsConfiguration((
                ourId, clock)-> HfcsServices.autoReplyTarget(ourId, clock, new InetSocketAddress(bindPort)));
    }

    public static HfcsConfiguration replyToSenderMode() {
        return replyToSenderMode(HfcsServices.DEFAULT_PORT);
    }

    public static HfcsConfiguration specificTargetMode(int bindPort, SocketAddress remote) {
        return new HfcsConfiguration((
                ourId, clock)-> HfcsServices.unicast(ourId, clock,
                new InetSocketAddress(bindPort),
                remote));
    }

    public static HfcsConfiguration specificTargetMode(SocketAddress remote) {
        return specificTargetMode(HfcsServices.DEFAULT_PORT, remote);
    }

    public static HfcsConfiguration multicastMode(int bindPort,
                                                  NetworkInterface networkInterface,
                                                  InetAddress group,
                                                  int remotePort) {
        return new HfcsConfiguration((
                ourId, clock)-> {
            assert networkInterface.supportsMulticast();
            return HfcsServices.multicast(ourId, clock,
                    new InetSocketAddress(bindPort),
                    remotePort,
                    networkInterface,
                    group);
        });
    }

    public static HfcsConfiguration broadcastMode(SocketAddress bindAddress,
                                                                       InetAddress broadcastAddress,
                                                                       int remotePort) {
        return new HfcsConfiguration((
                ourId, clock)-> HfcsServices.broadcast(ourId, clock,
                bindAddress,
                broadcastAddress,
                remotePort));
    }

    public static HfcsConfiguration broadcastMode(SocketAddress bindAddress,
                                                                       int remotePort) {
        return new HfcsConfiguration((
                ourId, clock)-> HfcsServices.broadcast(ourId, clock,
                bindAddress,
                remotePort));
    }

    public static HfcsConfiguration broadcastMode(InterfaceAddress interfaceAddress, int bindPort, int remotePort) {
        return broadcastMode(
                new InetSocketAddress(interfaceAddress.getAddress(), bindPort),
                interfaceAddress.getBroadcast(),
                remotePort);
    }

    public static HfcsConfiguration broadcastMode(int bindPort, int remotePort) {
        return broadcastMode(new InetSocketAddress(bindPort), remotePort);
    }
    
}
