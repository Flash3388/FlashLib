package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.OutMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RequestContentMessage implements InMessage, OutMessage {

    public static final MessageType TYPE = MessageType.create(100007, RequestContentMessage::readFrom);

    public RequestContentMessage() {
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {

    }

    private static RequestContentMessage readFrom(DataInput input) throws IOException {
        return new RequestContentMessage();
    }
}
