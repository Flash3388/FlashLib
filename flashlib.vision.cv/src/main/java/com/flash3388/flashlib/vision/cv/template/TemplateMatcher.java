package com.flash3388.flashlib.vision.cv.template;

import org.opencv.core.Mat;

public interface TemplateMatcher {

    TemplateMatchingResult match(Mat scene) throws TemplateMatchingException;
    ScaledTemplateMatchingResult matchWithScaling(Mat scene, double initialScaleFactor) throws TemplateMatchingException;
}
