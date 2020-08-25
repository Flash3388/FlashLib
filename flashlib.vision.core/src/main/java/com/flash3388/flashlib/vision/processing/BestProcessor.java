package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class BestProcessor<T> implements Processor<Stream<T>, Optional<T>> {

    private final Comparator<? super T> mComparator;

    public BestProcessor(Comparator<? super T> comparator) {
        mComparator = comparator;
    }

    @Override
    public Optional<T> process(Stream<T> input) throws VisionException {
        return input.max(mComparator);
    }
}
