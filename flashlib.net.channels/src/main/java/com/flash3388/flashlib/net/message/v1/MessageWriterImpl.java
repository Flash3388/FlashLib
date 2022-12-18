package com.flash3388.flashlib.net.message.v1;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataOutput;
import java.io.IOException;

public class MessageWriterImpl implements MessageWriter {

    private final InstanceId mOurId;

    public MessageWriterImpl(InstanceId ourId) {
        mOurId = ourId;
    }

    @Override
    public void write(DataOutput dataOutput, Message message) throws IOException {
        int typeKey = message.getType().getKey();

        MessageHeader header = new MessageHeader(mOurId, typeKey);
        header.writeTo(dataOutput);
        message.writeInto(dataOutput);
    }
}
