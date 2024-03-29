package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.analysis.Analyser;
import com.flash3388.flashlib.vision.analysis.Analysis;

import java.util.Optional;
import java.util.function.Consumer;

public class VisionPipeline<T, R> implements Pipeline<T> {

    public static class Builder<T, R> {

        private Processor<T, R> mProcessor;
        private Analyser<? super T, ? super R> mAnalyser;
        private Consumer<? super Analysis> mAnalysisConsumer;

        public Builder() {
            mProcessor = (t) -> {throw new AssertionError("unimplemented");};
            mAnalyser = (Analyser<T, R>) (input, input2) -> Optional.empty();
            mAnalysisConsumer = (a) -> {};
        }

        public Builder<T, R> process(Processor<T, R> processor) {
            mProcessor = processor;
            return this;
        }

        public Builder<T, R> analyse(Analyser<? super T, ? super R> analyser) {
            mAnalyser = analyser;
            return this;
        }

        public Builder<T, R> analysisTo(Consumer<? super Analysis> consumer) {
            mAnalysisConsumer = consumer;
            return this;
        }

        public VisionPipeline<T, R> build() {
            return new VisionPipeline<>(mProcessor, mAnalyser, mAnalysisConsumer);
        }
    }

    private final Processor<T, R> mProcessor;
    private final Analyser<? super T, ? super R> mAnalyser;
    private final Consumer<? super Analysis> mAnalysisConsumer;

    public VisionPipeline(Processor<T, R> processor, Analyser<? super T, ? super R> analyser,
                          Consumer<? super Analysis> analysisConsumer) {
        mProcessor = processor;
        mAnalyser = analyser;
        mAnalysisConsumer = analysisConsumer;
    }

    @Override
    public void process(T input) throws VisionException {
        R out = mProcessor.process(input);

        Optional<Analysis> analysis = mAnalyser.analyse(input, out);
        analysis.ifPresent(mAnalysisConsumer);
    }
}
