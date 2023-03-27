package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RequestContentMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100007, RequestContentMessage::new);

    public RequestContentMessage() {
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {

    }

    @Override
    public void readFrom(DataInput input) throws IOException {

    }
}
