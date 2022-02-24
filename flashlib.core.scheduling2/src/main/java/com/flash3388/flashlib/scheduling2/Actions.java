package com.flash3388.flashlib.scheduling2;

public class Actions {

    private Actions() {}

    public static GenericAction.Builder builder() {
        return new GenericAction.Builder();
    }

    public static Action empty() {
        return new GenericAction();
    }
}
