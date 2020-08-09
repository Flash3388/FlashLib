package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.HidChannel;

public class GenericHidChannel implements HidChannel {

    private final int mValue;

    public GenericHidChannel(int value) {
        mValue = value;
    }

    public int intValue() {
        return mValue;
    }
}
