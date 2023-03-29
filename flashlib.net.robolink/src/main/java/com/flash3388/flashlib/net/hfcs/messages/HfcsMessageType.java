package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.channels.messsaging.Message;
import com.flash3388.flashlib.net.channels.messsaging.MessageType;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;

import java.io.DataInput;
import java.io.IOException;

public class HfcsMessageType implements MessageType {

    public static final int KEY = 20000;

    private final KnownInDataTypes mInDataTypes;

    public HfcsMessageType(KnownInDataTypes inDataTypes) {
        mInDataTypes = inDataTypes;
    }

    @Override
    public int getKey() {
        return KEY;
    }

    @Override
    public Message parse(DataInput dataInput) throws IOException {
        InType<?> type = readType(dataInput);
        Object data = readFrom(dataInput, type);
        return new HfcsInMessage(type, data);
    }

    private InType<?> readType(DataInput input) throws IOException {
        int typeInt = input.readInt();
        return mInDataTypes.get(typeInt);
    }

    private Object readFrom(DataInput input, InType<?> type) throws IOException {
        Object data = type.readFrom(input);
        if (!type.getClassType().isInstance(data)) {
            throw new IOException("bad type returned from InType");
        }

        return data;
    }
}