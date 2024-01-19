package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageSerializer {

    private final InstanceId mOurId;
    private final Clock mClock;

    public MessageSerializer(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;
    }

    public byte[] serialize(Message message) throws IOException {
        MessageType type = message.getType();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            type.write(message, dataOutputStream);
        }

        byte[] content = outputStream.toByteArray();
        Time sendTime = mClock.currentTime();
        MessageHeader header = new MessageHeader(content.length, mOurId, type.getKey(), sendTime);

        outputStream = new ByteArrayOutputStream();
        try (DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            header.writeTo(dataOutputStream);
            dataOutputStream.write(content);
        }

        return outputStream.toByteArray();
    }
}
