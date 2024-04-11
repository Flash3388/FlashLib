package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.messaging.Message;

public class ReceivedMessage {

    public final MessageHeader header;
    public final Message message;

    public ReceivedMessage(MessageHeader header, Message message) {
        this.header = header;
        this.message = message;
    }
}
