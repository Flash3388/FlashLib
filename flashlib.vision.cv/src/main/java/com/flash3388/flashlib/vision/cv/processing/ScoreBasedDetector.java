package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Range;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ScoreBasedDetector implements ObjectDetector {

    private final CvProcessing mCvProcessing;
    private final Range mContourAreaRange;
    private final Range mScoreRange;

    public ScoreBasedDetector(CvProcessing cvProcessing, Range contourAreaRange, Range scoreRange) {
        mCvProcessing = cvProcessing;
        mContourAreaRange = contourAreaRange;
        mScoreRange = scoreRange;
    }

    @Override
    public Collection<? extends Scorable> detect(CvImage image, Function<MatOfPoint, ? extends Scorable> contourMaker) {
        List<MatOfPoint> contours = mCvProcessing.detectContours(image.getMat());
        return contours.stream()
                .filter((contour)-> isInRange(contour.width() * contour.height(), mContourAreaRange))
                .map(contourMaker)
                .filter((contour)-> isInRange(contour.score(), mScoreRange))
                .collect(Collectors.toList());
    }

    private boolean isInRange(double value, Range range) {
        return value <= range.end && value >= range.start;
    }
}
