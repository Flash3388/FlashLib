package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.vision.exceptions.VisionException;

public interface ImageSource<T extends Image> {

    T get() throws VisionException;
}
