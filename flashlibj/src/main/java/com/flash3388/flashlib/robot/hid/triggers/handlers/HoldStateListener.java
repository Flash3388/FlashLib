package com.flash3388.flashlib.robot.hid.triggers.handlers;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerStateListener;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class HoldStateListener implements TriggerStateListener {

    private final Action mAction;
    private final Clock mClock;
    private final Time mMinHeldTime;

    private Time mActiveStateTime;

    public HoldStateListener(Action action, Clock clock, Time minHeldTime) {
        mAction = action;
        mClock = clock;
        mMinHeldTime = minHeldTime;

        mActiveStateTime = Time.INVALID;
    }

    @Override
    public void onStateChange(TriggerState newState, TriggerState lastState) {
        switch (newState) {
            case ACTIVE: {
                mActiveStateTime = mClock.currentTime();
                break;
            }
            case INACTIVE: {
                if (mAction.isRunning()) {
                   mAction.cancel();
                }

               break;
            }
        }
    }

    @Override
    public void updateInState(TriggerState state) {
        if (!mActiveStateTime.isValid() || state != TriggerState.ACTIVE) {
            return;
        }

        Time timePassed = mClock.currentTime().sub(mActiveStateTime);

        if (timePassed.largerThanOrEquals(mMinHeldTime) && !mAction.isRunning()) {
            mAction.start();

            mActiveStateTime = Time.INVALID;
        }
    }
}
