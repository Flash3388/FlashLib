package edu.flash3388.flashlib.vision.camera;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImageSource;

public interface Camera extends ImageSource {

    int getFps();

    int getHeight();
    int getWidth();

    Image capture();

    @Override
    default Image get() {
        return capture();
    }
}
