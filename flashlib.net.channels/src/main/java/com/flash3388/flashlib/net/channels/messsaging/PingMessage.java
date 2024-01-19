package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

public class PingMessage implements Message {

    private static final MessageType TYPE = MessageType.create(1,
            (d)-> new PingMessage(),
            (m, d)-> {});

    @Override
    public MessageType getType() {
        return TYPE;
    }
}
