package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.hid.triggers.handlers.HoldStateHandler;
import com.flash3388.flashlib.robot.hid.triggers.handlers.PressStateHandler;
import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public abstract class HardwareButton extends Button {

    private static final Time MAX_PRESS_TIME = Time.milliseconds(200);

    private final Clock mClock;

    protected HardwareButton(Clock clock) {
        mClock = clock;
    }

    @Override
    public void whenPressed(Action action) {
        whenPressed(action, MAX_PRESS_TIME);
    }

    public void whenPressed(Action action, Time maxPressTime) {
        addStateHandler(new PressStateHandler(action, mClock, maxPressTime));
    }

    @Override
    public void whileHeld(Action action) {
        whileHeld(action, MAX_PRESS_TIME);
    }

    public void whileHeld(Action action, Time minHeldTime) {
        addStateHandler(new HoldStateHandler(action, mClock, minHeldTime));
    }
}
