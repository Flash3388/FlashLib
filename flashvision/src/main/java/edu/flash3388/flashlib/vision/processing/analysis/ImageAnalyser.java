package edu.flash3388.flashlib.vision.processing.analysis;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.processing.analysis.exceptions.ImageAnalysingException;

public interface ImageAnalyser<T extends Image> {

    Analysis analyse(T image) throws ImageAnalysingException;
}
