package com.flash3388.flashlib.robot.hid.generic;

import com.flash3388.flashlib.robot.hid.Pov;

public class GenericPov implements Pov {

    private final RawHidInterface mInterface;
    private final int mChannel;
    private final int mPov;

    public GenericPov(RawHidInterface anInterface, int channel, int pov) {
        mInterface = anInterface;
        mChannel = channel;
        mPov = pov;
    }

    @Override
    public int getAsInt() {
        return mInterface.getPovValue(mChannel, mPov);
    }
}
