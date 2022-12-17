package com.flash3388.flashlib.net.message;

import com.flash3388.flashlib.net.LocalNetInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.NoSuchElementException;

public class MessageSerializer {

    private final LocalNetInfo mOurInfo;
    private final KnownMessageTypes mMessageTypes;

    public MessageSerializer(LocalNetInfo ourInfo, KnownMessageTypes messageTypes) {
        mOurInfo = ourInfo;
        mMessageTypes = messageTypes;
    }

    public void write(DataOutput output, Message message) throws IOException {
        int typeKey = message.getType().getKey();

        MessageHeader header = new MessageHeader(mOurInfo.getId(), typeKey);
        header.writeTo(output);
        message.writeInto(output);
    }

    public MessageHeader readHeader(DataInput input) throws IOException {
        return new MessageHeader(input);
    }

    public Message read(DataInput dataInput, MessageHeader header) throws IOException {
        try {
            MessageType type = mMessageTypes.get(header.getMessageType());

            Message message = type.create();
            message.readFrom(dataInput);

            return message;
        } catch (NoSuchElementException e) {
            throw new IOException(e);
        }
    }
}
