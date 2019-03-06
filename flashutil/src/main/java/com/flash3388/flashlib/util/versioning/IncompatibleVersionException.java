package com.flash3388.flashlib.util.versioning;

public class IncompatibleVersionException extends RuntimeException {

    public IncompatibleVersionException(Version current, Version other) {
        super(String.format("Current: %s, Other: %s", current.toString(), other.toString()));
    }
}
