package com.flash3388.flashlib.robot.hid;

import java.util.function.IntSupplier;

/**
 * A wrapper for POVs on human interface devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public class Pov implements IntSupplier {

    private final HidInterface mHidInterface;
    private final int mChannel;
    private final int mPov;

    public Pov(HidInterface hidInterface, int channel, int pov) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mPov = pov;
    }

    @Override
    public int getAsInt() {
        return mHidInterface.getHidPov(mChannel, mPov);
    }
}
