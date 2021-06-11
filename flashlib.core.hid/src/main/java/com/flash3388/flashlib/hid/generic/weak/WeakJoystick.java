package com.flash3388.flashlib.hid.generic.weak;

import com.flash3388.flashlib.hid.Joystick;
import com.flash3388.flashlib.hid.Pov;
import com.flash3388.flashlib.hid.generic.GenericPov;
import com.flash3388.flashlib.hid.generic.RawHidInterface;

import java.util.ArrayList;
import java.util.Collections;

public class WeakJoystick extends WeakHid implements Joystick {

    public WeakJoystick(RawHidInterface hidInterface, int channel) {
        super(hidInterface, channel, new ArrayList<>(), new ArrayList<>(),
                Collections.singletonList(new GenericPov(hidInterface, channel, 0)));
    }

    @Override
    public Pov getPov(int index) {
        if (index != 0) {
            throw new IllegalArgumentException("Unknown pov " + index);
        }

        return super.getPov(index);
    }

    @Override
    public Pov getPov() {
        return getPov(0);
    }
}
