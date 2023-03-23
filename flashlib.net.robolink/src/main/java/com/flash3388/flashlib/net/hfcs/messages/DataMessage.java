package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.message.Message;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DataMessage implements Message {

    private final OutPackage mOutPackage;
    private final InPackage mInPackage;

    private InType<?> mInType;
    private Object mInData;

    public DataMessage(OutPackage outPackage) {
        mOutPackage = outPackage;
        mInPackage = null;
        mInType = null;
        mInData = null;
    }

    public DataMessage(InPackage inPackage) {
        mInPackage = inPackage;
        mOutPackage = null;
        mInType = null;
        mInData = null;
    }

    public InType<?> getInType() {
        return mInType;
    }

    public Object getInData() {
        return mInData;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        assert mOutPackage != null;
        mOutPackage.writeInto(output);
    }

    @Override
    public void readFrom(DataInput input) throws IOException {
        assert mInPackage != null;
        mInType = mInPackage.readType(input);
        mInData = mInPackage.readFrom(input, mInType);
    }
}
