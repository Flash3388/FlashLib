package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.net.hfcs.InType;

import java.io.DataInput;
import java.io.IOException;

public class HidDataInType implements InType<RawHidData> {

    private final DataStore mDataStore;
    private final HidDataSerializer mParser;

    public HidDataInType(DataStore dataStore) {
        mDataStore = dataStore;
        mParser = new HidDataSerializer();
    }

    @Override
    public Class<RawHidData> getClassType() {
        return RawHidData.class;
    }

    @Override
    public RawHidData readFrom(DataInput input) throws IOException {
        RawHidData rawHidData = mDataStore.retrieve();
        mParser.loadInto(input, rawHidData);
        return rawHidData;
    }

    @Override
    public int getKey() {
        return 2001;
    }
}
