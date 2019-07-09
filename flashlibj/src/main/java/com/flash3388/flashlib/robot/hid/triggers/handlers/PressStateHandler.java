package com.flash3388.flashlib.robot.hid.triggers.handlers;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerStateHandler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class PressStateHandler implements TriggerStateHandler {

    private final Action mAction;
    private final Clock mClock;
    private final Time mMaxTimePress;

    private Time mActiveStateTime;

    public PressStateHandler(Action action, Clock clock, Time maxTimePress) {
        mAction = action;
        mClock = clock;
        mMaxTimePress = maxTimePress;

        mActiveStateTime = Time.INVALID;
    }

    @Override
    public void handleStateChange(TriggerState newState, TriggerState lastState) {
        switch (newState) {
            case ACTIVE: {
                mActiveStateTime = mClock.currentTime();
                break;
            }
            case INACTIVE: {
                Time timePassed = mClock.currentTime().sub(mActiveStateTime);
                if (timePassed.lessThanOrEquals(mMaxTimePress) && !mAction.isRunning()) {
                    mAction.start();
                }
            }
        }
    }
}
