package com.flash3388.flashlib.app;

public class StartupException extends RuntimeException {

    public StartupException(Throwable cause) {
        super(cause);
    }

    public StartupException(String message) {
        super(message);
    }

    public StartupException() {
    }
}
