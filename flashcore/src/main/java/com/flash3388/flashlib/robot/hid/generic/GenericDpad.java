package com.flash3388.flashlib.robot.hid.generic;

import com.flash3388.flashlib.robot.hid.Button;
import com.flash3388.flashlib.robot.hid.Dpad;

public class GenericDpad extends GenericPov implements Dpad {

    private final Button mUp;
    private final Button mDown;
    private final Button mLeft;
    private final Button mRight;

    public GenericDpad(RawHidInterface anInterface, int channel, int pov) {
        super(anInterface, channel, pov);

        mUp = new PovButton(anInterface, channel, pov, PovRange.UP);
        mDown = new PovButton(anInterface, channel, pov, PovRange.DOWN);
        mLeft = new PovButton(anInterface, channel, pov, PovRange.LEFT);
        mRight = new PovButton(anInterface, channel, pov, PovRange.RIGHT);
    }

    @Override
    public Button up() {
        return mUp;
    }

    @Override
    public Button down() {
        return mDown;
    }

    @Override
    public Button left() {
        return mLeft;
    }

    @Override
    public Button right() {
        return mRight;
    }
}
