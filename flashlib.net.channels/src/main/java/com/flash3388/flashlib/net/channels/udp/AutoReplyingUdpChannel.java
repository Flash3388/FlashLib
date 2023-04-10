package com.flash3388.flashlib.net.channels.udp;

import com.flash3388.flashlib.net.channels.IncomingData;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class AutoReplyingUdpChannel extends UdpChannel {

    public AutoReplyingUdpChannel(SocketAddress bindAddress, Logger logger, Runnable onOpen) {
        super(bindAddress, logger, onOpen);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        IncomingData data = super.read(buffer);
        if (data.getSender() != null) {
            setRemoteAddress(data.getSender());
        }

        return data;
    }
}
