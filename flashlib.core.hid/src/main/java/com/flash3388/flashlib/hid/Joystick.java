package com.flash3388.flashlib.hid;

import java.util.Collections;

public interface Joystick extends Hid {

    default Axis getAxis(JoystickAxis axis) {
        return getAxis(axis.axisIndex());
    }

    default Button getButton(JoystickButton button) {
        return getButton(button.buttonIndex());
    }

    @Override
    default Pov getPov(int pov) {
        if (pov != 0) {
            throw new IllegalArgumentException("unknown pov " + pov);
        }
        return getPov();
    }

    @Override
    default int getPovCount() {
        return 1;
    }

    @Override
    default Iterable<Pov> povs() {
        return Collections.singleton(getPov());
    }

    Pov getPov();
}
