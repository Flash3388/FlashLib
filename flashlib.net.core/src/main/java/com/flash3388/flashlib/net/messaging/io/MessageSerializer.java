package com.flash3388.flashlib.net.messaging.io;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.NoSuchElementException;

public class MessageSerializer {

    private final KnownMessageTypes mMessageTypes;

    public MessageSerializer(KnownMessageTypes messageTypes) {
        mMessageTypes = messageTypes;
    }

    public void write(DataOutput output, Message message) throws IOException {
        int typeKey = message.getType().getKey();

        MessageHeader header = new MessageHeader(typeKey);
        header.writeTo(output);
        message.writeInto(output);
    }

    public Message read(DataInput dataInput) throws IOException {
        try {
            MessageHeader header = new MessageHeader(dataInput);
            MessageType type = mMessageTypes.get(header.getMessageType());

            Message message = type.create();
            message.readFrom(dataInput);

            return message;
        } catch (NoSuchElementException e) {
            throw new IOException(e);
        }
    }
}
