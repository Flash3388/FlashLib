package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

public class CvImage implements Image {

    private final Mat mMat;

    public CvImage(Mat mat) {
        mMat = mat;
    }

    public static CvImage fromBytes(byte[] bytes) {
        Mat mat = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        return new CvImage(mat);
    }

    public Mat getMat() {
        return mMat;
    }

    @Override
    public int getHeight() {
        return mMat.height();
    }

    @Override
    public int getWidth() {
        return mMat.width();
    }

    @Override
    public byte[] getRaw() {
        MatOfByte buffer = new MatOfByte();
        MatOfInt compressParams = new MatOfInt();

        try {
            Imgcodecs.imencode(".jpg", mMat, buffer, compressParams);
            byte[] imageArr = new byte[(int) (buffer.total() * buffer.elemSize())];
            buffer.get(0, 0, imageArr);
            return imageArr;
        } finally {
            compressParams.release();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        mMat.release();
    }
}
