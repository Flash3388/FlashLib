package com.flash3388.flashlib.vision.cv;

import com.flash3388.flashlib.vision.ImageCodec;
import com.flash3388.flashlib.vision.color.ColorDimension;
import com.flash3388.flashlib.vision.color.ColorRange;
import com.flash3388.flashlib.vision.color.ColorSpace;
import com.flash3388.flashlib.vision.color.DimensionRange;
import com.sun.tools.javac.util.Pair;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoWriter;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;

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

    private static CvImage filterInvertedColors(CvImage source, ColorRange filterRange,
                                                ColorDimension invertedDimension) {

        Pair<ColorRange, ColorRange> invertedRanges = createInvertedColorRanges(filterRange, invertedDimension);
        CvImage bottomThresh = filterColors(source, invertedRanges.fst);
        CvImage topThresh = filterColors(source, invertedRanges.snd);

        Mat combinedMat = new Mat();
        Core.add(bottomThresh.getMat(), topThresh.getMat(), combinedMat);

        return new CvImage(combinedMat, bottomThresh.getColorSpace());
    }

    private static Pair<ColorRange, ColorRange> createInvertedColorRanges(
            ColorRange originalRange, ColorDimension dimensionToInvert) {
        List<DimensionRange> bottomDimensionRanges = new ArrayList<>(originalRange.getDimensions());
        List<DimensionRange> topDimensionRanges = new ArrayList<>(originalRange.getDimensions());

        OptionalInt indexToInvert = findDimensionRangeIndexByDimension(originalRange.getDimensions(),
                dimensionToInvert);

        if (!indexToInvert.isPresent()) {
            throw new IllegalArgumentException("range doesn't contain " + dimensionToInvert.name());
        }

        DimensionRange bottomRange = new DimensionRange(dimensionToInvert,
                dimensionToInvert.getValueRange().getMin(),
                originalRange.getDimensions().get(indexToInvert.getAsInt()).getMin());

        DimensionRange topRange = new DimensionRange(dimensionToInvert,
                originalRange.getDimensions().get(indexToInvert.getAsInt()).getMax(),
                dimensionToInvert.getValueRange().getMax());

        bottomDimensionRanges.set(indexToInvert.getAsInt(), bottomRange);
        topDimensionRanges.set(indexToInvert.getAsInt(), topRange);

        return new Pair<>(
                new ColorRange(originalRange.getColorSpace(), bottomDimensionRanges),
                new ColorRange(originalRange.getColorSpace(), topDimensionRanges));
    }

    public static OptionalInt findDimensionRangeIndexByDimension(List<DimensionRange> ranges,
                                                                       ColorDimension colorDimension) {
        return IntStream.range(0, ranges.size())
                .filter(i -> ranges.get(i).getDimension().equals(colorDimension))
                .findAny();

    }

    public static CvImage filterColors(CvImage source, ColorRange filterRange, EnumSet<CvOpOption> options) {
        if (source.getColorSpace() != filterRange.getColorSpace()) {
            throw new IllegalArgumentException("range and image do not share color space");
        }

        Mat dst = source.getMat();
        if (options.contains(CvOpOption.PRESERVE_ORIGINAL_MAT)) {
            dst = new Mat();
        }



        if (filterRange.getDimensions().stream().anyMatch(DimensionRange::isInverted)) {
            ColorDimension firstInverted = filterRange.getDimensions().stream()
                    .filter(DimensionRange::isInverted)
                    .findFirst().get().getDimension();

            CvImage thresh = filterInvertedColors(source, filterRange, firstInverted);
            thresh.getMat().copyTo(dst);
            return thresh;
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
    public static void filterContoursByShape(List<MatOfPoint> contours, int vertices, double accuracy){
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

    private static int getColorSpaceConversionCode(ColorSpace src, ColorSpace dst) {
        if (src == ColorSpace.BGR && dst == ColorSpace.HSV) {
            return Imgproc.COLOR_BGR2HSV;
        }
        if (src == ColorSpace.BGR && dst == ColorSpace.RGB) {
            return Imgproc.COLOR_BGR2RGB;
        }

        throw new UnsupportedOperationException("no code for color space conversion");
    }
}
