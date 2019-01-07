package edu.flash3388.flashlib.vision.cam.jpeg;

import edu.flash3388.flashlib.cam.jpeg.JpegCamera;
import edu.flash3388.flashlib.cam.jpeg.JpegImage;

public class StaticImageJpegCamera implements JpegCamera {

    private final JpegImage mJpegImage;

    public StaticImageJpegCamera(JpegImage jpegImage) {
        mJpegImage = jpegImage;
    }

    @Override
    public JpegImage capture() {
        return mJpegImage;
    }

    @Override
    public int getHeight() {
        return mJpegImage.getHeight();
    }

    @Override
    public int getWidth() {
        return mJpegImage.getWidth();
    }

    @Override
    public int getFps() {
        return 30;
    }
}
