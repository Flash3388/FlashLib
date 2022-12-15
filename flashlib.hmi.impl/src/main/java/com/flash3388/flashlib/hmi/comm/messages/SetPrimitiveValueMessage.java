package com.flash3388.flashlib.hmi.comm.messages;

import com.castle.io.TypedSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class SetPrimitiveValueMessage {

    private final String mNamespace;
    private final Object mValue;

    public SetPrimitiveValueMessage(DataInput input, TypedSerializer serializer) throws IOException {
        mNamespace = input.readUTF();
        mValue = serializer.readTyped(input);
    }

    public String getNamespace() {
        return mNamespace;
    }

    public Object getValue() {
        return mValue;
    }

    public void write(DataOutput dataOutput, TypedSerializer serializer) throws IOException {
        dataOutput.writeUTF(mNamespace);
        serializer.writeTyped(dataOutput, mValue);
    }
}
