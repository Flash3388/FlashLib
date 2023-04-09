package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvHelper;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.processing.Processor;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RectProcessor implements Processor<CvImage, Stream<Rect>> {

    private final Predicate<? super Rect> mRectFilter;

    public RectProcessor(Predicate<? super Rect> rectFilter) {
        mRectFilter = rectFilter;
    }

    @Override
    public Stream<Rect> process(CvImage image) {
        List<MatOfPoint> contours = CvHelper.detectContours(image);
        return contours.stream()
                .map(Imgproc::boundingRect)
                .filter(mRectFilter);
    }
}
