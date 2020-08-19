package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.ImagePipeline;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public class VisionPipeline<T extends Image> implements ImagePipeline<T> {

    public static class Builder<T extends Image> {

        private final Collection<ImageProcessor<T>> mImageProcessors;
        private ImageAnalyser<T> mImageAnalyser;
        private Consumer<? super Analysis> mAnalysisConsumer;

        public Builder() {
            mImageProcessors = new ArrayList<>();
            mImageAnalyser = (t) -> Optional.empty();
            mAnalysisConsumer = (a) -> {};
        }

        public Builder<T> addProcessors(Collection<? extends ImageProcessor<T>> processors) {
            mImageProcessors.addAll(processors);
            return this;
        }

        @SafeVarargs
        public final Builder<T> addProcessors(ImageProcessor<T>... processors) {
            return addProcessors(Arrays.asList(processors));
        }

        public Builder<T> analyser(ImageAnalyser<T> analyser) {
            mImageAnalyser = analyser;
            return this;
        }

        public Builder<T> analysisTo(Consumer<? super Analysis> analysisConsumer) {
            mAnalysisConsumer = analysisConsumer;
            return this;
        }

        public VisionPipeline<T> build() {
            return new VisionPipeline<T>(mImageAnalyser, mAnalysisConsumer, mImageProcessors);
        }
    }

    private final ImageAnalyser<T> mImageAnalyser;
    private final Consumer<? super Analysis> mAnalysisConsumer;
    private final Collection<ImageProcessor<T>> mImageProcessors;

    public VisionPipeline(ImageAnalyser<T> imageAnalyser, Consumer<? super Analysis> analysisConsumer, Collection<ImageProcessor<T>> imageProcessors) {
        mImageAnalyser = imageAnalyser;
        mAnalysisConsumer = analysisConsumer;
        mImageProcessors = new ArrayList<>(imageProcessors);
    }

    @SafeVarargs
    public VisionPipeline(ImageAnalyser<T> imageAnalyser, Consumer<? super Analysis> analysisConsumer, ImageProcessor<T> ... imageProcessors) {
        this(imageAnalyser, analysisConsumer, Arrays.asList(imageProcessors));
    }

    @Override
    public void process(T image) throws ImageProcessingException {
        try {
            for (ImageProcessor<T> processor : mImageProcessors) {
                image = processor.process(image);
            }

            Optional<Analysis> analysis = mImageAnalyser.analyse(image);
            analysis.ifPresent(mAnalysisConsumer);
        } catch (ImageAnalysingException e) {
            throw new ImageProcessingException(e);
        }
    }
}
