package com.flash3388.flashlib.net.hfcs.messages;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;

import java.io.DataInput;
import java.io.IOException;

public class InPackage {

    private final KnownInDataTypes mInDataTypes;

    public InPackage(KnownInDataTypes inDataTypes) {
        mInDataTypes = inDataTypes;
    }

    public InType<?> readType(DataInput input) throws IOException {
        int typeInt = input.readInt();
        return mInDataTypes.get(typeInt);
    }

    public Object readFrom(DataInput input, InType<?> type) throws IOException {
        Object data = type.readFrom(input);
        if (!type.getClassType().isInstance(data)) {
            throw new IOException("bad type returned from InType");
        }

        return data;
    }
}
