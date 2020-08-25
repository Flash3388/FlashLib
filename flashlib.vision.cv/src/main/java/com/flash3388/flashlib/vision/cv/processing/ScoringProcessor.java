package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.processing.ProcessingException;
import com.flash3388.flashlib.vision.processing.Processor;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ScoringProcessor<T, R extends Scorable> implements Processor<Stream<T>, Stream<R>> {

    private final Function<T, R> mScorableFactory;
    private final Predicate<? super R> mScorableFilter;

    public ScoringProcessor(Function<T, R> scorableFactory, Predicate<? super R> scorableFilter) {
        mScorableFactory = scorableFactory;
        mScorableFilter = scorableFilter;
    }

    @Override
    public Stream<R> process(Stream<T> input) throws ProcessingException {
        return input.map(mScorableFactory)
                .filter(mScorableFilter);
    }
}
