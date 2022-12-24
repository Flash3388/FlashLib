package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.util.resources.CircularResourceHolder;

public class DataStore extends CircularResourceHolder<RawHidData> {

    public DataStore() {
        super(3, RawHidData::new);
    }
}
