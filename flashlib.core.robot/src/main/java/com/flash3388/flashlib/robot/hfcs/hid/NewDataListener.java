package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.hid.data.RawHidData;
import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.util.resources.CircularResourceHolder;

public class NewDataListener implements DataListener<RawHidData> {

    private final HidData mHidData;
    private final CircularResourceHolder<RawHidData> mDataStore;

    public NewDataListener(HidData hidData, CircularResourceHolder<RawHidData> dataStore) {
        mHidData = hidData;
        mDataStore = dataStore;
    }

    @Override
    public void onReceived(DataReceivedEvent<RawHidData> event) {
        RawHidData old = mHidData.loadNewData(event.getData());
        mDataStore.add(old);
    }
}
