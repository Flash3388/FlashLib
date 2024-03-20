package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.hid.data.HidDataSerializer;
import com.flash3388.flashlib.hid.data.RawHidData;
import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.util.resources.CircularResourceHolder;

import java.io.DataOutput;
import java.io.IOException;

public class HidOutData implements Serializable {

    private final HidData mHidData;
    private final CircularResourceHolder<RawHidData> mDataStore;
    private final HidDataSerializer mSerializer;

    public HidOutData(HidData hidData, CircularResourceHolder<RawHidData> dataStore) {
        mHidData = hidData;
        mDataStore = dataStore;
        mSerializer = new HidDataSerializer();

        // to load first into hidData
        swapData();
        mHidData.clearChannels();
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
