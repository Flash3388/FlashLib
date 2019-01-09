package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.vision.processing.ImageProcessingException;

public interface ImagePipeline<T extends Image> {

    void process(T image) throws ImageProcessingException;
}
