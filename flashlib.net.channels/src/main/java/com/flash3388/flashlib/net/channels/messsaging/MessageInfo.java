package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageInfo {

    private final InstanceId mSender;
    private final MessageType mType;

    public MessageInfo(InstanceId sender, MessageType type) {
        mSender = sender;
        mType = type;
    }

    public MessageInfo(DataInput dataInput, KnownMessageTypes messageTypes) throws IOException {
        mSender = InstanceId.createFrom(dataInput);
        int typeKey = dataInput.readInt();
        mType = messageTypes.get(typeKey);
    }

    public InstanceId getSender() {
        return mSender;
    }

    public MessageType getType() {
        return mType;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        mSender.writeTo(dataOutput);
        dataOutput.writeInt(mType.getKey());
    }
}
