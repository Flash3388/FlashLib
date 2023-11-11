package com.flash3388.flashlib.io;

public class UnsupportedChannelException extends RuntimeException {

    public UnsupportedChannelException() {
        super();
    }

    public UnsupportedChannelException(String message) {
        super(message);
    }

    public UnsupportedChannelException(Throwable cause) {
        super(cause);
    }

    public UnsupportedChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}
