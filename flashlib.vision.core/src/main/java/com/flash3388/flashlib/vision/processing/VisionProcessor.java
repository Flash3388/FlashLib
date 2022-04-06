package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.analysis.Analyser;
import com.flash3388.flashlib.vision.analysis.Analysis;

import java.util.Optional;

public class VisionProcessor<T, R> implements Processor<T, Optional<Analysis>> {

    public static class Builder<T, R> {

        private Processor<T, R> mProcessor;
        private Analyser<? super T, ? super R> mAnalyser;

        public Builder() {
            mProcessor = (t) -> {throw new AssertionError("unimplemented");};
            mAnalyser = (Analyser<T, R>) (input, input2) -> Optional.empty();
        }

        public Builder<T, R> process(Processor<T, R> processor) {
            mProcessor = processor;
            return this;
        }

        public Builder<T, R> analyse(Analyser<? super T, ? super R> analyser) {
            mAnalyser = analyser;
            return this;
        }

        public VisionProcessor<T, R> build() {
            return new VisionProcessor<>(mProcessor, mAnalyser);
        }
    }

    private final Processor<T, R> mProcessor;
    private final Analyser<? super T, ? super R> mAnalyser;

    public VisionProcessor(Processor<T, R> processor, Analyser<? super T, ? super R> analyser) {
        mProcessor = processor;
        mAnalyser = analyser;
    }

    @Override
    public Optional<Analysis> process(T input) throws VisionException {
        R out = mProcessor.process(input);
        return mAnalyser.analyse(input, out);
    }
}
