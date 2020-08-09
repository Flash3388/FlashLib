package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Button;
import com.flash3388.flashlib.hid.Pov;

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

    @Override
    public Button asButton() {
        return new PovButton(mInterface, mChannel, mPov, PovRange.FULL);
    }
}
