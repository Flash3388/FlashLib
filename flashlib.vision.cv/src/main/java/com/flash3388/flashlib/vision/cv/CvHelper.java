package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.ImageCodec;
import com.flash3388.flashlib.vision.color.ColorRange;
import com.flash3388.flashlib.vision.color.ColorSpace;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class CvHelper {

    private CvHelper() {}

    public enum CvOpOption {
        PRESERVE_ORIGINAL_MAT
    }

    public static Scalar arrayToScalar(int[] arr) {
        double[] values = {0, 0, 0, 0};
        for (int i = 0; i < arr.length; i++) {
            values[i] = arr[i];
        }

        return new Scalar(values);
    }

    public static CvImage filterColors(CvImage source, ColorRange filterRange, EnumSet<CvOpOption> options) {
        if (source.getColorSpace() != filterRange.getColorSpace()) {
            throw new IllegalArgumentException("range and image do not share color space");
        }

        Mat dst = source.getMat();
        if (options.contains(CvOpOption.PRESERVE_ORIGINAL_MAT)) {
            dst = new Mat();
        }

        Core.inRange(source.getMat(),
                arrayToScalar(filterRange.getMinAsArray()),
                arrayToScalar(filterRange.getMaxAsArray()),
                dst);

        return new CvImage(dst, source.getColorSpace());
    }

    public static CvImage filterColors(CvImage source, ColorRange filterRange) {
        return filterColors(source, filterRange, EnumSet.noneOf(CvOpOption.class));
    }

    public static CvImage convertColorSpace(CvImage image, ColorSpace dst, EnumSet<CvOpOption> options) {
        if (image.getColorSpace() == dst) {
            throw new IllegalArgumentException("Cannot convert to same space");
        }

        Mat dstMat = image.getMat();
        if (options.contains(CvOpOption.PRESERVE_ORIGINAL_MAT)) {
            dstMat = new Mat();
        }

        int code = getColorSpaceConversionCode(image.getColorSpace(), dst);
        Imgproc.cvtColor(image.getMat(), dstMat, code);

        return new CvImage(dstMat, dst);
    }

    public static CvImage convertColorSpace(CvImage image, ColorSpace dst) {
        return convertColorSpace(image, dst, EnumSet.noneOf(CvOpOption.class));
    }

    public static int codecToFourccCode(ImageCodec codec) {
        int code;
        switch (codec) {
            case MJPEG:
                code = VideoWriter.fourcc('M', 'J', 'P', 'G');
                break;
            case YUYV:
                code = VideoWriter.fourcc('Y', 'U', 'Y', 'V');
                break;
            default:
                throw new IllegalArgumentException("Unknown codec");
        }

        return code;
    }

    public static ImageCodec fourccCodeToCodec(int code) {
        for (ImageCodec codec : ImageCodec.values()) {
            int codecCode = codecToFourccCode(codec);
            if (codecCode == code) {
                return codec;
            }
        }

        throw new IllegalArgumentException("Unknown codec");
    }

    /**
     * Detects contours within a binary image.
     *
     * @param threshold the binary image
     * @param contours list of contours to fill
     * @param hierarchy hierarchy of contours
     *
     * @return the contours param
     *
     * @see Imgproc#findContours(Mat, List, Mat, int, int)
     */
    public static List<MatOfPoint> detectContours(CvImage threshold, List<MatOfPoint> contours, Mat hierarchy) {
        Imgproc.findContours(threshold.getMat(), contours, hierarchy,
                Imgproc.RETR_CCOMP,
                Imgproc.CHAIN_APPROX_SIMPLE);
        return contours;
    }

    /**
     * Detects contours within a binary image.
     *
     * @param threshold the binary image
     *
     * @return the contours list
     *
     * @see Imgproc#findContours(Mat, List, Mat, int, int)
     */
    public static List<MatOfPoint> detectContours(CvImage threshold){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        return detectContours(threshold, contours, hierarchy);
    }

    /**
     * Filters contours by shape. Iterates through the list of contours and approximates their shape.
     * Compares the vertices of the shape to the desired vertices and removes the contour if they do not match.
     *
     * @param contours list of contours
     * @param vertices vertices of the desired shape
     * @param accuracy the accuracy of approximation
     *
     * @see Imgproc#approxPolyDP(MatOfPoint2f, MatOfPoint2f, double, boolean)
     */
    public static void detectContoursByShape(List<MatOfPoint> contours, int vertices, double accuracy){
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f();
        MatOfPoint2f approxCurve = new MatOfPoint2f();

        for(int idx = contours.size() - 1; idx >= 0; idx--){
            MatOfPoint contour = contours.get(idx);

            matOfPoint2f.fromList(contour.toList());
            Imgproc.approxPolyDP(matOfPoint2f, approxCurve, Imgproc.arcLength(matOfPoint2f, true) * accuracy, true);
            long total = approxCurve.total();

            if (total != vertices) {
                contours.remove(idx);
            }
        }
    }

    /**
     * Resizes the given image by a given factor. If the scale factor is positive, the image is enlarged, otherwise
     * it's size is decreased.
     *
     * @param img the image to resize
     * @param scaleFactor the size factor in pixels
     */
    public static void resize(Mat img, double scaleFactor){
        Imgproc.resize(img, img, new Size(0,0), scaleFactor, scaleFactor, Imgproc.INTER_CUBIC);
    }

    private static int getColorSpaceConversionCode(ColorSpace src, ColorSpace dst) {
        switch (src) {
            case BGR: {
                switch (dst) {
                    case HSV: return Imgproc.COLOR_BGR2HSV;
                    case RGB: return Imgproc.COLOR_BGR2RGB;
                    default:
                        throw new UnsupportedOperationException("no code for color space conversion");
                }
            }
            case RGB: {
                switch (dst) {
                    case HSV: return Imgproc.COLOR_RGB2HSV;
                    case BGR: return Imgproc.COLOR_RGB2BGR;
                    default:
                        throw new UnsupportedOperationException("no code for color space conversion");
                }
            }
            case HSV: {
                switch (dst) {
                    case RGB: return Imgproc.COLOR_HSV2RGB;
                    case BGR: return Imgproc.COLOR_HSV2BGR;
                    default:
                        throw new UnsupportedOperationException("no code for color space conversion");
                }
            }
            default:
                throw new UnsupportedOperationException("no code for color space conversion");
        }
    }
}
