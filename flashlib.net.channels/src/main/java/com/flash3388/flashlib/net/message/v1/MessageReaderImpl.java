package com.flash3388.flashlib.net.message.v1;

import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageType;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageReaderImpl implements MessageReader {

    private final KnownMessageTypes mMessageTypes;

    public MessageReaderImpl(KnownMessageTypes messageTypes) {
        mMessageTypes = messageTypes;
    }

    @Override
    public Result read(DataInput dataInput) throws IOException {
        MessageHeader header = new MessageHeader(dataInput);
        byte[] content = new byte[header.getContentSize()];
        dataInput.readFully(content);

        MessageType type = mMessageTypes.get(header.getMessageType());

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            Message message = type.create();
            message.readFrom(dataInputStream);

            return new Result(header.getSenderId(), type, message);
        }
    }
}
