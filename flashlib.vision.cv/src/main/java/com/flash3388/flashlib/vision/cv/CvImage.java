package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.Image;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

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
    public boolean isEmpty() {
        return mMat.empty();
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
    public java.awt.Image toAwt() {
        // based on https://riptutorial.com/opencv/example/21963/converting-an-mat-object-to-an-bufferedimage-object
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mMat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mMat.channels() * mMat.cols() * mMat.rows();
        byte[] pixels = new byte[bufferSize];
        mMat.get(0, 0, pixels); // get all the pixels

        BufferedImage image = new BufferedImage(mMat.cols(), mMat.rows(), type);
        byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, targetPixels, 0, pixels.length);

        return image;
    }

    @Override
    protected void finalize() throws Throwable {
        mMat.release();
    }
}
