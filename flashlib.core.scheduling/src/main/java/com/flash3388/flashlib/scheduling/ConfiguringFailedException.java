package com.flash3388.flashlib.scheduling;

public class ConfiguringFailedException extends RuntimeException {

    private final ActionInterface mAction;

    public ConfiguringFailedException(ActionInterface action, Throwable cause) {
        super("Error while configuring action", cause);
        mAction = action;
    }

    public ActionInterface getAction() {
        return mAction;
    }
}
