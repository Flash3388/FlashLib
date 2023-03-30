package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.udp.AutoReplyingUdpChannel;
import com.flash3388.flashlib.net.channels.udp.BroadcastUdpChannel;
import com.flash3388.flashlib.net.channels.udp.MulticastUdpChannel;
import com.flash3388.flashlib.net.channels.udp.UdpChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;

public class HfcsServices {

    public static final int DEFAULT_PORT = Constants.DEFAULT_PORT;

    private HfcsServices() {}

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

    public static HfcsServiceBase autoReplyTarget(InstanceId ourId, Clock clock, int bindPort) {
        return new HfcsServiceImpl(ourId, clock, (onConn)-> new AutoReplyingUdpChannel(bindPort, LOGGER, onConn));
    }

    public static HfcsServiceBase unicast(InstanceId ourId, Clock clock, int bindPort, SocketAddress remote) {
        return new HfcsServiceImpl(ourId, clock, (onConn)-> new UdpChannel(remote, bindPort, LOGGER, onConn));
    }

    public static HfcsServiceBase multicast(InstanceId ourId, Clock clock, int bindPort,
                                            int remotePort,
                                            NetworkInterface multicastInterface,
                                            InetAddress multicastGroup) {
        return new HfcsServiceImpl(ourId, clock,
                (onConn)-> new MulticastUdpChannel(multicastInterface, multicastGroup,
                        bindPort, remotePort, LOGGER, onConn));
    }

    public static HfcsServiceBase broadcast(InstanceId ourId, Clock clock, int bindPort, int remotePort) {
        return new HfcsServiceImpl(ourId, clock, (onConn)-> new BroadcastUdpChannel(remotePort, bindPort, LOGGER, onConn));
    }
}
