package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.ImageCodec;
import com.flash3388.flashlib.vision.ImageIsEmptyException;
import com.flash3388.flashlib.vision.SourceReadFailedException;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.color.ColorSpace;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class CvCamera implements Camera<CvImage> {

    private final VideoCapture mVideoCapture;

    public CvCamera(VideoCapture videoCapture) {
        mVideoCapture = videoCapture;
    }

    public CvCamera(int dev) {
        this(new VideoCapture(dev));
    }

    public CvCamera(int dev, int apiType) {
        this(new VideoCapture(dev, apiType));
    }

    public CvCamera(int dev, CameraBackend backend) {
        this(dev, backend.getCode());
    }

    @Override
    public int getFps() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FPS);
    }

    public void setFps(int value) throws VisionException {
        setProperty(Videoio.CAP_PROP_FPS, value);
    }

    @Override
    public int getHeight() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
    }

    public void setHeight(int value) throws VisionException {
        setProperty(Videoio.CAP_PROP_FRAME_HEIGHT, value);
    }

    @Override
    public int getWidth() {
        return (int) mVideoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
    }

    public void setWidth(int value) throws VisionException {
        setProperty(Videoio.CAP_PROP_FRAME_WIDTH, value);
    }

    public ImageCodec getCodec() {
        int code = (int) mVideoCapture.get(Videoio.CAP_PROP_FOURCC);
        return CvHelper.fourccCodeToCodec(code);
    }

    public void setCodec(ImageCodec codec) throws VisionException {
        int code = CvHelper.codecToFourccCode(codec);
        setProperty(Videoio.CAP_PROP_FOURCC, code);
    }

    @Override
    public CvImage capture() throws VisionException {
        Mat mat = new Mat();
        if (!mVideoCapture.read(mat)) {
            mat.release();
            throw new SourceReadFailedException();
        }

        if (mat.empty()) {
            mat.release();
            throw new ImageIsEmptyException();
        }

        return new CvImage(mat, ColorSpace.BGR);
    }

    @Override
    public void close() throws Exception {
        mVideoCapture.release();
    }

    private void setProperty(int code, double value) throws VisionException {
        mVideoCapture.set(code, value);

        double set = mVideoCapture.get(code);
        if (set != value) {
            throw new VisionException("Failed to set property value");
        }
    }
}
