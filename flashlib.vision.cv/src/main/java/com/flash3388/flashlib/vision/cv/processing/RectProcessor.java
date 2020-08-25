package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.ProcessingException;
import com.flash3388.flashlib.vision.processing.Processor;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RectProcessor implements Processor<CvImage, Stream<Rect>> {

    private final CvProcessing mCvProcessing;
    private final Predicate<? super Rect> mRectFilter;

    public RectProcessor(CvProcessing cvProcessing, Predicate<? super Rect> rectFilter) {
        mCvProcessing = cvProcessing;
        mRectFilter = rectFilter;
    }

    @Override
    public Stream<Rect> process(CvImage image) throws ProcessingException {
        List<MatOfPoint> contours = mCvProcessing.detectContours(image.getMat());
        return contours.stream()
                .map(Imgproc::boundingRect)
                .filter(mRectFilter);
    }
}
