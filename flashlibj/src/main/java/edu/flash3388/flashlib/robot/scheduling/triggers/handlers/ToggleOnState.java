package edu.flash3388.flashlib.robot.scheduling.triggers.handlers;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerState;
import edu.flash3388.flashlib.robot.scheduling.triggers.TriggerStateHandler;

public class ToggleOnState implements TriggerStateHandler {

    private final TriggerState mTriggerState;
    private final Action mAction;

    public ToggleOnState(TriggerState triggerState, Action action) {
        mTriggerState = triggerState;
        mAction = action;
    }

    @Override
    public void handleState(TriggerState state) {
        if (mTriggerState == state) {
            if (mAction.isRunning()) {
                mAction.cancel();
            } else {
                mAction.start();
            }
        }
    }
}
