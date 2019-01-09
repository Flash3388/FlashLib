package edu.flash3388.flashlib.vision.processing;

import edu.flash3388.flashlib.vision.VisionException;

public class ImageProcessingException extends VisionException {

    public ImageProcessingException(Throwable cause) {
        super(cause);
    }

    public ImageProcessingException(String message) {
        super(message);
    }

    public ImageProcessingException() {
    }
}
