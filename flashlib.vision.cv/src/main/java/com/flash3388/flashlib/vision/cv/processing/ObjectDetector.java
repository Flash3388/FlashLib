package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import org.opencv.core.MatOfPoint;

import java.util.Collection;
import java.util.function.Function;

public interface ObjectDetector {

    Collection<? extends Scorable> detect(CvImage image, Function<MatOfPoint, ? extends Scorable> contourMaker);
}
