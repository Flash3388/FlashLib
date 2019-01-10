package edu.flash3388.flashlib.vision.processing;

import edu.flash3388.flashlib.vision.Image;

public interface ImageProcessor<T extends Image> {

    T process(T image) throws ImageProcessingException;
}