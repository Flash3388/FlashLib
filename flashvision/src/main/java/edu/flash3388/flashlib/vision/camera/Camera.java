package edu.flash3388.flashlib.vision.camera;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImageSource;

import java.util.Optional;

public interface Camera extends ImageSource {

    int getFps();

    int getHeight();
    int getWidth();

    Image capture();

    @Override
    default Optional<Image> get() {
        return Optional.of(capture());
    }
}
