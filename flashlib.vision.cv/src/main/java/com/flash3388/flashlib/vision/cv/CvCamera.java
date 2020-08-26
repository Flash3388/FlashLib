package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.VisionException;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CvCamera implements Camera<CvImage> {

    private final VideoCapture mVideoCapture;

    public CvCamera(VideoCapture videoCapture) {
        mVideoCapture = videoCapture;
    }

    public CvCamera(int dev) {
        this(new VideoCapture(0));
    }

    @Override
    public int getFps() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FPS);
    }

    @Override
    public int getHeight() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
    }

    @Override
    public int getWidth() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
    }

    @Override
    public CvImage capture() throws VisionException {
        Mat mat = new Mat();
        if (!mVideoCapture.read(mat)) {
            mat.release();
            throw new VisionException("failed to read");
        }

        if (mat.empty()) {
            mat.release();
            throw new VisionException("image is empty");
        }

        return new CvImage(mat);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
    }

    @Override
    public void close() throws Exception {
        mVideoCapture.release();
    }
}
