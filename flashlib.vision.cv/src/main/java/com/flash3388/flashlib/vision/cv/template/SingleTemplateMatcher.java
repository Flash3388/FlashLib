package com.flash3388.flashlib.vision.cv.template;

import com.flash3388.flashlib.vision.cv.CvHelper;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

public class SingleTemplateMatcher implements TemplateMatcher {

    private final Mat mTemplate;
    private final TemplateMatchingMethod mMatchingMethod;

    public SingleTemplateMatcher(Mat template, TemplateMatchingMethod matchingMethod) {
        mTemplate = template;
        mMatchingMethod = matchingMethod;
    }

    @Override
    public TemplateMatchingResult match(Mat scene) throws TemplateMatchingException {
        int resultColumns = scene.cols() - mTemplate.cols() + 1;
        int resultRows = scene.rows() - mTemplate.rows() + 1;

        Mat result = new Mat(resultRows, resultColumns, CvType.CV_32FC1);

        Imgproc.matchTemplate(scene, mTemplate, result, mMatchingMethod.value());

        return getMatchingResultFromMat(result);
    }

    @Override
    public ScaledTemplateMatchingResult matchWithScaling(Mat scene, double initialScaleFactor) throws TemplateMatchingException {
        int templateWidth = mTemplate.width();
        int templateHeight = mTemplate.height();

        double currentScaleFactor = initialScaleFactor;
        ScaledTemplateMatchingResult bestMatchingResult = null;

        for (Mat copyScene = scene.clone();
             copyScene.height() > templateHeight && copyScene.width() > templateWidth;
             CvHelper.resize(copyScene, currentScaleFactor)) {

            TemplateMatchingResult templateMatchingResult = match(copyScene);

            if (bestMatchingResult == null || templateMatchingResult.compareTo(bestMatchingResult) > 0) {
                bestMatchingResult = new ScaledTemplateMatchingResult(
                        templateMatchingResult.getCenterPoint(),
                        templateMatchingResult.getScore(),
                        currentScaleFactor);
            }

            currentScaleFactor *= initialScaleFactor;
        }

        return bestMatchingResult;
    }

    private TemplateMatchingResult getMatchingResultFromMat(Mat result) {
        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(result);

        Point matchLocation;
        double maxValue;

        if (mMatchingMethod == TemplateMatchingMethod.SQDIFF || mMatchingMethod == TemplateMatchingMethod.SQDIFF_NORMED) {
            matchLocation = minMaxLocResult.minLoc;
            maxValue = minMaxLocResult.minVal;
        } else {
            matchLocation = minMaxLocResult.maxLoc;
            maxValue = minMaxLocResult.maxVal;
        }

        return new TemplateMatchingResult(
                new Point(matchLocation.x + mTemplate.cols() * 0.5, matchLocation.y + mTemplate.rows() * 0.5),
                maxValue);
    }
}
