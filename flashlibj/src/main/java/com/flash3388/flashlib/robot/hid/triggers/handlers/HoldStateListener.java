package com.flash3388.flashlib.robot.hid.triggers.handlers;

import com.flash3388.flashlib.robot.RunningRobot;
import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import com.flash3388.flashlib.robot.scheduling.triggers.TriggerStateHandler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class HoldStateHandler implements TriggerStateHandler {

    private final Action mAction;
    private final Clock mClock;
    private final Time mMinHeldTime;

    private Time mActiveStateTime;

    public HoldStateHandler(Action action, Clock clock, Time minHeldTime) {
        mAction = action;
        mClock = clock;
        mMinHeldTime = minHeldTime;

        mActiveStateTime = Time.INVALID;
    }

    @Override
    public void handleStateChange(TriggerState newState, TriggerState lastState) {
        switch (newState) {
            case ACTIVE: {
                if (!mActiveStateTime.isValid()) {
                    RunningRobot.INSTANCE.get().getLogger().debug("invalid active state");
                    mActiveStateTime = mClock.currentTime();
                    break;
                }

                Time timePassed = mClock.currentTime().sub(mActiveStateTime);

                RunningRobot.INSTANCE.get().getLogger().debug("time passed: {}", mActiveStateTime.valueAsMillis());
                if (timePassed.largerThanOrEquals(mMinHeldTime) && !mAction.isRunning()) {
                    RunningRobot.INSTANCE.get().getLogger().debug("starting action");
                    mAction.start();

                    mActiveStateTime = Time.INVALID;
                }

                break;
            }
            case INACTIVE: {
                RunningRobot.INSTANCE.get().getLogger().debug("canceling action: {}", mAction.isRunning());
                if (mAction.isRunning()) {
                   mAction.cancel();
                }

               break;
            }
        }
    }
}
