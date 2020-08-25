package com.flash3388.flashlib.vision.cv.processing.analysis;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.template.ScaledTemplateMatchingResult;
import com.flash3388.flashlib.vision.cv.template.SingleTemplateMatcher;
import com.flash3388.flashlib.vision.cv.template.TemplateMatchingException;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

public class TemplateMatchingAnalyser implements ImageAnalyser<CvImage> {

    private final SingleTemplateMatcher mTemplateMatcher;
    private final DoubleSupplier mScaleFactor;
    private final Function<ScaledTemplateMatchingResult, Analysis> mAnalysisFactory;

    public TemplateMatchingAnalyser(SingleTemplateMatcher templateMatcher, DoubleSupplier scaleFactor,
                                    Function<ScaledTemplateMatchingResult, Analysis> analysisFactory) {
        mTemplateMatcher = templateMatcher;
        mScaleFactor = scaleFactor;
        mAnalysisFactory = analysisFactory;
    }

    public TemplateMatchingAnalyser(SingleTemplateMatcher templateMatcher, DoubleSupplier scaleFactor) {
        this(templateMatcher, scaleFactor, new DefaultAnalysisFactory());
    }

    public TemplateMatchingAnalyser(SingleTemplateMatcher templateMatcher, double scaleFactor,
                                    Function<ScaledTemplateMatchingResult, Analysis> analysisFactory) {
        this(templateMatcher, Suppliers.of(scaleFactor), analysisFactory);
    }

    public TemplateMatchingAnalyser(SingleTemplateMatcher templateMatcher, double scaleFactor) {
        this(templateMatcher, Suppliers.of(scaleFactor));
    }

    @Override
    public Optional<Analysis> analyse(CvImage image) throws ImageAnalysingException {
        try {
            ScaledTemplateMatchingResult templateMatchingResult = mTemplateMatcher.matchWithScaling(image.getMat(), mScaleFactor.getAsDouble());
            Analysis analysis = mAnalysisFactory.apply(templateMatchingResult);
            return Optional.of(analysis);
        } catch (TemplateMatchingException e) {
            throw new ImageAnalysingException(e);
        }
    }

    private static class DefaultAnalysisFactory implements Function<ScaledTemplateMatchingResult, Analysis> {

        @Override
        public Analysis apply(ScaledTemplateMatchingResult scaledTemplateMatchingResult) {
            return new Analysis.Builder()
                    .put("center.x", scaledTemplateMatchingResult.getCenterPoint().x)
                    .put("center.y", scaledTemplateMatchingResult.getCenterPoint().y)
                    .put("score", scaledTemplateMatchingResult.getScore())
                    .put("scaleFactor", scaledTemplateMatchingResult.getScaleFactor())
                    .build();
        }
    }
}
