package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Dpad;
import com.flash3388.flashlib.hid.DualshockAxis;
import com.flash3388.flashlib.hid.DualshockButton;
import com.flash3388.flashlib.hid.DualshockController;
import com.flash3388.flashlib.hid.Pov;

import java.util.Collections;

public class GenericDualshockController extends GenericHid implements DualshockController {

    private final Dpad mDpad;

    public GenericDualshockController(RawHidInterface rawHidInterface, int channel) {
        super(rawHidInterface, channel, DualshockAxis.count(), DualshockButton.count(), 0);

        mDpad = new GenericDpad(rawHidInterface, channel, 0);
    }

    @Override
    public Pov getPov(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Unknown pov " + index);
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
        return mDpad;
    }
}
