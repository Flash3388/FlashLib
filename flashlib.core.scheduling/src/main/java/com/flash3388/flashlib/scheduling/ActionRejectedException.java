package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;

public class ActionRejectedException extends RuntimeException {

    private final Action mAction;

    public ActionRejectedException(Action action, String reason) {
        super(String.format("Action %s rejected because: %s", action, reason));
        mAction = action;
    }

    public Action getAction() {
        return mAction;
    }
}
