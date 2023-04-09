package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.messaging.OutMessage;

import java.io.DataOutput;
import java.io.IOException;

public class HfcsOutMessage implements OutMessage {

    private final Type mOutType;
    private final OutData mOutData;

    public HfcsOutMessage(Type outType, OutData outData) {
        mOutType = outType;
        mOutData = outData;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mOutType.getKey());
        mOutData.writeInto(output);
    }
}
