package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.Optional;
import java.util.function.Consumer;

public class VisionPipeline<T extends Image, R extends Image> implements Pipeline<T> {

    public static class Builder<T extends Image, R extends Image> {

        private Processor<T, R> mProcessor;
        private ImageAnalyser<? super R> mImageAnalyser;
        private Consumer<? super Analysis> mAnalysisConsumer;

        public Builder() {
            mProcessor = (t) -> {throw new AssertionError("unimplemented");};
            mImageAnalyser = (t) -> Optional.empty();
            mAnalysisConsumer = (a) -> {};
        }

        public Builder<T, R> process(Processor<T, R> processor) {
            mProcessor = processor;
            return this;
        }

        public Builder<T, R> analyse(ImageAnalyser<? super R> analyser) {
            mImageAnalyser = analyser;
            return this;
        }

        public Builder<T, R> analysisTo(Consumer<? super Analysis> consumer) {
            mAnalysisConsumer = consumer;
            return this;
        }

        public VisionPipeline<T, R> build() {
            return new VisionPipeline<>(mProcessor, mImageAnalyser, mAnalysisConsumer);
        }
    }

    private final Processor<T, R> mProcessor;
    private final ImageAnalyser<? super R> mImageAnalyser;
    private final Consumer<? super Analysis> mAnalysisConsumer;

    public VisionPipeline(Processor<T, R> processor, ImageAnalyser<? super R> imageAnalyser, Consumer<? super Analysis> analysisConsumer) {
        mProcessor = processor;
        mImageAnalyser = imageAnalyser;
        mAnalysisConsumer = analysisConsumer;
    }

    @Override
    public void process(T image) throws ProcessingException {
        try {
            R outImage = mProcessor.process(image);

            Optional<Analysis> analysis = mImageAnalyser.analyse(outImage);
            analysis.ifPresent(mAnalysisConsumer);
        } catch (ImageAnalysingException e) {
            throw new ProcessingException(e);
        }
    }
}
