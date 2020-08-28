package com.flash3388.flashlib.hid;

import java.util.Collections;

public interface XboxController extends Hid {

    default Axis getAxis(XboxAxis axis) {
        return getAxis(axis.axisIndex());
    }

    default Button getButton(XboxButton button) {
        return getButton(button.buttonIndex());
    }

    @Override
    default Pov getPov(int pov) {
        if (pov != 0) {
            throw new IllegalArgumentException("unknown pov " + pov);
        }
        return getDpad();
    }

    @Override
    default int getPovCount() {
        return 1;
    }

    @Override
    default Iterable<Pov> povs() {
        return Collections.singleton(getDpad());
    }

    Dpad getDpad();
}
