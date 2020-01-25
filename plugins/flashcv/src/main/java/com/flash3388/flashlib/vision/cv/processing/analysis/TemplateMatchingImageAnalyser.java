package com.flash3388.flashlib.vision.cv.processing.analysis;

import com.beans.DoubleProperty;
import com.beans.properties.atomic.AtomicDoubleProperty;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.template.SingleTemplateMatcher;
import com.flash3388.flashlib.vision.cv.template.TemplateMatchingException;
import com.google.gson.JsonObject;
import com.flash3388.flashlib.vision.cv.template.ScaledTemplateMatchingResult;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.HashMap;
import java.util.Map;

public class TemplateMatchingImageAnalyser implements ImageAnalyser<CvImage> {

    private final SingleTemplateMatcher mTemplateMatcher;
    private final DoubleProperty mScaleFactor;

    public TemplateMatchingImageAnalyser(SingleTemplateMatcher templateMatcher, DoubleProperty scaleFactor) {
        mTemplateMatcher = templateMatcher;
        mScaleFactor = scaleFactor;
    }

    public TemplateMatchingImageAnalyser(SingleTemplateMatcher templateMatcher, double scaleFactor) {
        this(templateMatcher, new AtomicDoubleProperty(scaleFactor));
    }

    public DoubleProperty scaleFactorProperty() {
        return mScaleFactor;
    }

    @Override
    public Analysis analyse(CvImage image) throws ImageAnalysingException {
        try {
            ScaledTemplateMatchingResult templateMatchingResult = mTemplateMatcher.matchWithScaling(image.getMat(), mScaleFactor.getAsDouble());
            return createAnalysisFromMatchingResult(templateMatchingResult);
        } catch (TemplateMatchingException e) {
            throw new ImageAnalysingException(e);
        }
    }

    private Analysis createAnalysisFromMatchingResult(ScaledTemplateMatchingResult templateMatchingResult) {
        Map<String, Object> data = new HashMap<>();
        data.put("centerX", templateMatchingResult.getCenterPoint().x);
        data.put("centerY", templateMatchingResult.getCenterPoint().y);
        data.put("matchingScore", templateMatchingResult.getScore());
        data.put("scaleFactor", templateMatchingResult.getScaleFactor());

        return new Analysis(data);
    }
}
