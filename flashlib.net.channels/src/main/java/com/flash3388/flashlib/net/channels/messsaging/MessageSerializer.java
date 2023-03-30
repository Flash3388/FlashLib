package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageSerializer {

    private final InstanceId mOurId;

    public MessageSerializer(InstanceId ourId) {
        mOurId = ourId;
    }

    public byte[] serialize(MessageType type, OutMessage message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            MessageInfo info = new MessageInfo(mOurId, type);
            info.writeTo(dataOutputStream);
            message.writeInto(dataOutputStream);
        }

        byte[] content = outputStream.toByteArray();
        MessageHeader header = new MessageHeader(content.length);

        outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);
        }

        return outputStream.toByteArray();
    }
}
