package com.flash3388.flashlib.vision.cv.processing.analysis;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.template.ScaledTemplateMatchingResult;
import com.flash3388.flashlib.vision.cv.template.SingleTemplateMatcher;
import com.flash3388.flashlib.vision.processing.analysis.Analyser;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;

import java.util.Optional;
import java.util.function.DoubleSupplier;
import java.util.function.Function;

public class TemplateMatchingAnalyser<T> implements Analyser<T, CvImage> {

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
    public Optional<Analysis> analyse(T original, CvImage input) throws VisionException {
        ScaledTemplateMatchingResult templateMatchingResult = mTemplateMatcher.matchWithScaling(input.getMat(), mScaleFactor.getAsDouble());
        Analysis analysis = mAnalysisFactory.apply(templateMatchingResult);
        return Optional.of(analysis);
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
