package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class ChannelId implements Serializable {

    public static final int SIZE = Long.BYTES + InstanceId.BYTES;

    private final InstanceId mInstanceId;
    private final long mMessengerId;

    public ChannelId(InstanceId instanceId, long messengerId) {
        mInstanceId = instanceId;
        mMessengerId = messengerId;
    }

    public ChannelId(DataInput dataInput) throws IOException {
        mInstanceId = InstanceId.createFrom(dataInput);
        mMessengerId = dataInput.readLong();
    }

    public InstanceId getInstanceId() {
        return mInstanceId;
    }

    public long getMessengerId() {
        return mMessengerId;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        mInstanceId.writeTo(output);
        output.writeLong(mMessengerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChannelId channelId = (ChannelId) o;
        return mMessengerId == channelId.mMessengerId && Objects.equals(mInstanceId, channelId.mInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mInstanceId, mMessengerId);
    }

    @Override
    public String toString() {
        return String.format("ChannelId{instanceId=%s, messengerId=%s}",
                mInstanceId.toString(), Long.toHexString(mMessengerId));
    }
}
