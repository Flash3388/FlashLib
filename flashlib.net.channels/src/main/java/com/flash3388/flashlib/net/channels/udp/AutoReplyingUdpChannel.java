package com.flash3388.flashlib.net.channels.udp;

import com.flash3388.flashlib.net.channels.IncomingData;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AutoReplyingUdpChannel extends UdpChannel {

    public AutoReplyingUdpChannel(int bindPort, Logger logger, Runnable onOpen) {
        super(bindPort, logger, onOpen);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        IncomingData data = super.read(buffer);
        setRemoteAddress(data.getSender());

        return data;
    }
}
