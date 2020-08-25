package com.flash3388.flashlib.vision.cv.processing.analysis;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class RectScoringAnalyser implements ImageAnalyser<CvImage> {

    public static class Builder {
        private final CvProcessing mCvProcessing;
        private final Function<? super Rect, ? extends Scorable> mScorableFactory;
        private final BiFunction<CvImage, ? super Scorable, ? extends Analysis> mAnalysisFactory;

        private Predicate<? super Rect> mRectFilter;
        private Predicate<? super Scorable> mScorableFilter;

        public Builder(CvProcessing cvProcessing,
                       Function<? super Rect, ? extends Scorable> scorableFactory,
                       BiFunction<CvImage, ? super Scorable, ? extends Analysis> analysisFactory) {
            mCvProcessing = cvProcessing;
            mScorableFactory = scorableFactory;
            mAnalysisFactory = analysisFactory;

            mRectFilter = (r)->true;
            mScorableFilter = (s)->true;
        }

        public Builder rectFilter(Predicate<? super Rect> filter) {
            mRectFilter = filter;
            return this;
        }

        public Builder rectAreaFilter(double minArea) {
            return rectFilter((r)-> r.area() > minArea);
        }

        public Builder scorableFilter(Predicate<? super Scorable> filter) {
            mScorableFilter = filter;
            return this;
        }

        public Builder scorableMinScoreFilter(double minScore) {
            return scorableFilter((s) -> s.score() > minScore);
        }

        public RectScoringAnalyser build() {
            return new RectScoringAnalyser(mCvProcessing,
                    mRectFilter, mScorableFactory,
                    mScorableFilter, mAnalysisFactory);
        }
    }

    private final CvProcessing mCvProcessing;
    private final Predicate<? super Rect> mRectFilter;
    private final Function<? super Rect, ? extends Scorable> mScorableFactory;
    private final Predicate<? super Scorable> mScorableFilter;
    private final BiFunction<CvImage, ? super Scorable, ? extends Analysis> mAnalysisFactory;

    public RectScoringAnalyser(CvProcessing cvProcessing, Predicate<? super Rect> rectFilter,
                               Function<? super Rect, ? extends Scorable> scorableFactory,
                               Predicate<? super Scorable> scorableFilter,
                               BiFunction<CvImage, ? super Scorable, ? extends Analysis> analysisFactory) {
        mCvProcessing = cvProcessing;
        mRectFilter = rectFilter;
        mScorableFactory = scorableFactory;
        mScorableFilter = scorableFilter;
        mAnalysisFactory = analysisFactory;
    }

    @Override
    public Optional<Analysis> analyse(CvImage image) throws ImageAnalysingException {
        List<MatOfPoint> contours = mCvProcessing.detectContours(image.getMat());
        Optional<? extends Scorable> bestFind = findBest(contours);

        if (!bestFind.isPresent()) {
            return Optional.empty();
        }

        Scorable scorable = bestFind.get();
        return Optional.of(mAnalysisFactory.apply(image, scorable));
    }

    private Optional<? extends Scorable> findBest(List<MatOfPoint> contours) {
        return makeRects(contours)
                .filter(mRectFilter)
                .map(mScorableFactory)
                .filter(mScorableFilter)
                .max(Comparator.comparingDouble(Scorable::score));
    }

    private Stream<Rect> makeRects(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}
