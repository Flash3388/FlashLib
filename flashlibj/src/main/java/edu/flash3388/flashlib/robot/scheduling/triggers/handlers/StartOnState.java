package edu.flash3388.flashlib.robot.scheduling.triggers.handlers;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerStateHandler;

public class StartOnState implements TriggerStateHandler {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public StartOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void handleState(TriggerState state) {
        if (mTriggerState == state) {
            mAction.start();
        }
    }
}
