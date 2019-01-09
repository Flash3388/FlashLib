package edu.flash3388.flashlib.vision.camera;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImageSource;
import edu.flash3388.flashlib.vision.exceptions.VisionException;

import java.util.Optional;

public interface Camera<T extends Image> extends ImageSource<T> {

    int getFps();

    int getHeight();
    int getWidth();

    T capture() throws VisionException;

    @Override
    default T get() throws VisionException {
        return capture();
    }
}
