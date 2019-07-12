package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.robot.hid.triggers.handlers.HoldStateListener;
import com.flash3388.flashlib.robot.hid.triggers.handlers.PressStateListener;
import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public abstract class HardwareButton extends Button {

    public static final Time DEFAULT_MAX_PRESS_TIME = Time.milliseconds(100);

    private final Clock mClock;
    private final Time mDefaultPressTime;

    protected HardwareButton(Clock clock, Time defaultPressTime) {
        mClock = clock;
        mDefaultPressTime = defaultPressTime;
    }

    protected HardwareButton(Clock clock) {
        this(clock, DEFAULT_MAX_PRESS_TIME);
    }

    @Override
    public void whenPressed(Action action) {
        whenPressed(action, mDefaultPressTime);
    }

    public void whenPressed(Action action, Time maxPressTime) {
        addStateListener(new PressStateListener(action, mClock, maxPressTime));
    }

    @Override
    public void whileHeld(Action action) {
        whileHeld(action, mDefaultPressTime);
    }

    public void whileHeld(Action action, Time minHeldTime) {
        addStateListener(new HoldStateListener(action, mClock, minHeldTime));
    }
}
