package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.udp.AutoReplyingUdpChannel;
import com.flash3388.flashlib.net.channels.udp.BroadcastUdpChannel;
import com.flash3388.flashlib.net.channels.udp.MulticastUdpChannel;
import com.flash3388.flashlib.net.channels.udp.UdpChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;

public class HfcsServices {

    public static final int DEFAULT_PORT = Constants.DEFAULT_PORT;

    private HfcsServices() {}

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

    public static HfcsServiceBase autoReplyTarget(InstanceId ourId, Clock clock, SocketAddress bindAddress) {
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new AutoReplyingUdpChannel(bindAddress, LOGGER, onConn));
    }

    public static HfcsServiceBase unicast(InstanceId ourId, Clock clock, SocketAddress bindAddress, SocketAddress remote) {
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new UdpChannel(remote, bindAddress, LOGGER, onConn));
    }

    public static HfcsServiceBase multicast(InstanceId ourId,
                                            Clock clock,
                                            SocketAddress bindAddress,
                                            int remotePort,
                                            NetworkInterface multicastInterface,
                                            InetAddress multicastGroup) {
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new MulticastUdpChannel(
                        multicastInterface,
                        multicastGroup,
                        remotePort,
                        bindAddress,
                        LOGGER,
                        onConn));
    }

    public static HfcsServiceBase broadcast(InstanceId ourId,
                                            Clock clock,
                                            SocketAddress bindAddress,
                                            InetAddress broadcastAddress,
                                            int remotePort) {
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new BroadcastUdpChannel(broadcastAddress, remotePort, bindAddress, LOGGER, onConn));
    }

    public static HfcsServiceBase broadcast(InstanceId ourId,
                                            Clock clock,
                                            SocketAddress bindAddress,
                                            int remotePort) throws IOException {
        InetAddress address = InetAddress.getByName("255.255.255.255");
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new BroadcastUdpChannel(address, remotePort, bindAddress, LOGGER, onConn));
    }
}
