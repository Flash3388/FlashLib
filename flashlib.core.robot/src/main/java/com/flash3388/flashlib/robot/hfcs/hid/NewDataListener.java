package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.hid.data.RawHidData;
import com.flash3388.flashlib.net.hfcs.HfcsInListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.TimeoutEvent;
import com.flash3388.flashlib.util.resources.CircularResourceHolder;
import org.slf4j.Logger;

public class NewDataListener implements HfcsInListener<RawHidData> {

    private final HidData mHidData;
    private final CircularResourceHolder<RawHidData> mDataStore;
    private final Logger mLogger;

    public NewDataListener(HidData hidData, CircularResourceHolder<RawHidData> dataStore, Logger logger) {
        mHidData = hidData;
        mDataStore = dataStore;
        mLogger = logger;
    }

    @Override
    public void onReceived(DataReceivedEvent<RawHidData> event) {
        RawHidData old = mHidData.loadNewData(event.getData());
        mDataStore.add(old);
    }

    @Override
    public void onTimeout(TimeoutEvent<RawHidData> event) {
        mLogger.warn("HID Data from HFCS not received in a while and packet has timed out. Resetting data");
        mHidData.clearChannels();
    }
}
