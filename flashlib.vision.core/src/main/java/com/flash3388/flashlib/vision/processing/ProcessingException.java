package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

public class ProcessingException extends VisionException {

    public ProcessingException(Throwable cause) {
        super(cause);
    }

    public ProcessingException(String message) {
        super(message);
    }

    public ProcessingException() {
    }
}
