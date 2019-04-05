package com.flash3388.flashlib.robot.scheduling;

public enum ExecutionOrder {
    SEQUENTIAL {
        @Override
        protected boolean canStartNextAction(ActionGroup actionGroup) {
            return !actionGroup.areAnyActionsRunning();
        }
    },
    PARALLEL {
        @Override
        protected boolean canStartNextAction(ActionGroup actionGroup) {
            return true;
        }
    };

    protected abstract boolean canStartNextAction(ActionGroup actionGroup);
}
