package com.flash3388.flashlib.vision.control.message;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StartMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1111,
            StartMessage::readFrom,
            StartMessage::writeInto);

    @Override
    public MessageType getType() {
        return TYPE;
    }

    private static StartMessage readFrom(DataInput dataInput) throws IOException {
        return new StartMessage();
    }

    private static void writeInto(Message message, DataOutput dataOutput) throws IOException {

    }
}
