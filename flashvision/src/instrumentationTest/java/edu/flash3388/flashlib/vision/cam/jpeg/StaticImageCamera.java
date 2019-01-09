package edu.flash3388.flashlib.vision.cam.jpeg;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.camera.Camera;

import java.util.Optional;

public class StaticImageCamera implements Camera {

    private final Image mImage;

    public StaticImageCamera(Image image) {
        mImage = image;
    }

    @Override
    public int getHeight() {
        return mImage.getHeight();
    }

    @Override
    public int getWidth() {
        return mImage.getWidth();
    }

    @Override
    public Image capture() {
        return mImage;
    }

    @Override
    public int getFps() {
        return 30;
    }
}
