package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.Type;

import java.io.DataOutput;
import java.io.IOException;

public class OutPackage {

    private final Type mOutType;
    private final OutData mOutData;

    public OutPackage(Type outType, OutData outData) {
        mOutType = outType;
        mOutData = outData;
    }

    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mOutType.getKey());
        mOutData.writeInto(output);
    }
}
