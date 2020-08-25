package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.processing.Processor;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class BestScoreProcessor<T extends Scorable> implements Processor<Stream<T>, Optional<T>> {

    @Override
    public Optional<T> process(Stream<T> input) {
        return input.max(Comparator.comparingDouble(Scorable::score));
    }
}
