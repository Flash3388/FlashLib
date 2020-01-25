package com.flash3388.flashlib.vision.cv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CvProcessing {

    /**
     * Filters mat data by colors. Data within the color boundary now represents a contour.
     *
     * @param source the source image
     * @param result the resulting binary image
     * @param range1 range for the first color dimension (Hue/Red)
     * @param range2 range for the seconds color dimension (Saturation/Green)
     * @param range3 range for the third color dimension (Value/Blue)
     *
     * @see Core#inRange(Mat, Scalar, Scalar, Mat)
     */
    public void filterMatColors(Mat source, Mat result, Range range1, Range range2, Range range3) {
        Core.inRange(
                source,
                new Scalar(range1.start, range2.start, range3.start),
                new Scalar(range1.end, range2.end, range3.end),
                result);
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
    public List<MatOfPoint> detectContours(Mat threshold, List<MatOfPoint> contours, Mat hierarchy) {
        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
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
    public List<MatOfPoint> detectContours(Mat threshold){
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
    public void detectContoursByShape(List<MatOfPoint> contours, int vertices, double accuracy){
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
    public void resize(Mat img, double scaleFactor){
        Imgproc.resize(img, img, new Size(0,0), scaleFactor, scaleFactor, Imgproc.INTER_CUBIC);
    }
}
