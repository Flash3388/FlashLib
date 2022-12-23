package com.flash3388.flashlib.net.message.v1;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageWriterImpl implements MessageWriter {

    private final InstanceId mOurId;

    public MessageWriterImpl(InstanceId ourId) {
        mOurId = ourId;
    }

    @Override
    public void write(DataOutput dataOutput, MessageType type, Message message) throws IOException {
        int typeKey = type.getKey();

        byte[] buffer;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            message.writeInto(dataOutputStream);
            dataOutputStream.flush();

            buffer = outputStream.toByteArray();
        }

        MessageHeader header = new MessageHeader(mOurId, typeKey, buffer.length);
        header.writeTo(dataOutput);
        dataOutput.write(buffer);
    }
}
