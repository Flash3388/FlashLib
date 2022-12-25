package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.util.resources.CircularResourceHolder;

import java.io.DataInput;
import java.io.IOException;

public class HidDataInType extends HidDataType implements InType<RawHidData> {

    private final CircularResourceHolder<RawHidData> mDataStore;
    private final HidDataSerializer mParser;

    public HidDataInType(CircularResourceHolder<RawHidData> dataStore) {
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
}
