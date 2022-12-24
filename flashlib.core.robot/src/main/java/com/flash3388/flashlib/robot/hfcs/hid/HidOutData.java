package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.net.hfcs.OutData;

import java.io.DataOutput;
import java.io.IOException;

public class HidOutData implements OutData {

    private final HidData mHidData;
    private final DataStore mDataStore;
    private final HidDataSerializer mSerializer;

    public HidOutData(HidData hidData, DataStore dataStore) {
        mHidData = hidData;
        mDataStore = dataStore;
        mSerializer = new HidDataSerializer();

        // to load first into hidData
        swapData();
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        RawHidData data = swapData();
        mSerializer.writeFrom(output, data);
    }

    private RawHidData swapData() {
        RawHidData rawHidData = mDataStore.retrieve();
        RawHidData old = mHidData.replace(rawHidData);
        if (old != null) {
            mDataStore.add(old);
        }

        return rawHidData;
    }
}
