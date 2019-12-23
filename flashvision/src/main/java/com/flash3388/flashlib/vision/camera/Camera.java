package com.flash3388.flashlib.vision.camera;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.ImageSource;
import com.flash3388.flashlib.vision.VisionException;

public interface Camera<T extends Image> extends ImageSource<T>, AutoCloseable {

    int getFps();

    int getHeight();
    int getWidth();

    T capture() throws VisionException;

    @Override
    default T get() throws VisionException {
        return capture();
    }
}
