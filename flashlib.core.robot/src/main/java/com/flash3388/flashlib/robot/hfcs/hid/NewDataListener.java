package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;

public class NewDataListener implements DataListener<RawHidData> {

    private final HidData mHidData;
    private final DataStore mDataStore;

    public NewDataListener(HidData hidData, DataStore dataStore) {
        mHidData = hidData;
        mDataStore = dataStore;
    }

    @Override
    public void onReceived(DataReceivedEvent<RawHidData> event) {
        RawHidData old = mHidData.loadNewData(event.getData());
        mDataStore.add(old);
    }
}
