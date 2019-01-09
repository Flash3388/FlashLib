package edu.flash3388.flashlib.vision.processing.analysis;

import edu.flash3388.flashlib.vision.Image;

public interface ImageAnalyser<T extends Image> {

    Analysis analyse(T image) throws ImageAnalysingException;
}
