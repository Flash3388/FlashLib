package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.cv.CvHelper;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.detection.ObjectDetector;
import com.flash3388.flashlib.vision.detection.RealTargetConfig;
import com.flash3388.flashlib.vision.detection.ScorableTarget;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SizeScoreBasedDetector implements ObjectDetector<CvImage, ScorableTarget> {

    private final RealTargetConfig mRealTargetConfig;
    private final DetectorLimits mLimits;

    public SizeScoreBasedDetector(RealTargetConfig realTargetConfig, DetectorLimits limits) {
        mRealTargetConfig = realTargetConfig;
        mLimits = limits;
    }

    @Override
    public Collection<? extends ScorableTarget> detect(CvImage image) {
        List<MatOfPoint> contours = CvHelper.detectContours(image);
        return retrieveTargets(contours);
    }

    private Collection<ScorableTarget> retrieveTargets(List<MatOfPoint> contours) {
        return rectifyContours(contours)
                .filter(rect -> rect.area() >= mLimits.getMinAcceptableSizePixels())
                .map(rect -> new RatioTarget(rect, mRealTargetConfig))
                .filter(target -> target.score() >= mLimits.getMinAcceptableScore())
                .collect(Collectors.toList());
    }

    private Stream<Rect> rectifyContours(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}
