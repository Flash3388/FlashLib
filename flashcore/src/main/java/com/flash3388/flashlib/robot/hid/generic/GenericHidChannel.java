package com.flash3388.flashlib.robot.hid.generic;

import com.flash3388.flashlib.robot.hid.HidChannel;

public class GenericHidChannel implements HidChannel {

    private final int mValue;

    public GenericHidChannel(int value) {
        mValue = value;
    }

    public int intValue() {
        return mValue;
    }
}
