package edu.flash3388.flashlib.vision.camera;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImageSource;

import java.util.Optional;

public interface Camera<T extends Image> extends ImageSource<T> {

    int getFps();

    int getHeight();
    int getWidth();

    Optional<T> capture();

    @Override
    default Optional<T> get() {
        return capture();
    }
}
