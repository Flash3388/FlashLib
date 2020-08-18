package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Dpad;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.XboxAxis;
import com.flash3388.flashlib.hid.XboxButton;
import com.flash3388.flashlib.hid.XboxController;

import java.util.Collections;

public class GenericXboxController extends GenericHid implements XboxController {

    private static final int DPAD_POV_INDEX = 0;

    private final Dpad mDpad;

    public GenericXboxController(RawHidInterface rawHidInterface, int channel) {
        super(rawHidInterface, channel, XboxAxis.count(), XboxButton.count(), 0);

        mDpad = new GenericDpad(rawHidInterface, channel, DPAD_POV_INDEX);
    }

    @Override
    public Pov getPov(int pov) {
        if (pov != DPAD_POV_INDEX) {
            throw new IllegalArgumentException("Unknown pov " + pov);
        }
        return mDpad;
    }
    @Override
    public int getPovCount() {
        return 1;
    }
    @Override
    public Iterable<Pov> povs() {
        return Collections.singleton(getDpad());
    }

    @Override
    public Dpad getDpad() {
        return null;
    }
}