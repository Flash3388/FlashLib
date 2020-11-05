package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StreamMappingProcessor<T, R> implements Processor<Stream<T>, Stream<R>> {

    private final Function<T, R> mMapper;
    private final Predicate<? super R> mFilter;

    public StreamMappingProcessor(Function<T, R> mapper, Predicate<? super R> filter) {
        mMapper = mapper;
        mFilter = filter;
    }

    @Override
    public Stream<R> process(Stream<T> input) throws VisionException {
        return input.map(mMapper)
                .filter(mFilter);
    }
}
