package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxButton;

import java.util.Collections;

public class GenericJoystick extends GenericHid implements Joystick {

    private final Pov mPov;

    public GenericJoystick(RawHidInterface rawHidInterface, int channel) {
        super(rawHidInterface, channel, XboxAxis.count(), XboxButton.count(), 0);

        mPov = new GenericPov(rawHidInterface, channel, 0);
    }

    @Override
    public Pov getPov(int pov) {
        if (pov != 0) {
            throw new IllegalArgumentException("Unknown pov " + pov);
        }
        return mPov;
    }

    @Override
    public int getPovCount() {
        return 1;
    }

    @Override
    public Iterable<Pov> povs() {
        return Collections.singleton(mPov);
    }

    @Override
    public Pov getPov() {
        return mPov;
    }
}
