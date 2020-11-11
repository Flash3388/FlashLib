package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

import java.util.function.Function;

public class MappingProcessor<T, R> implements Processor<T, R> {

    private final Function<? super T, ? extends R> mMapper;

    public MappingProcessor(Function<? super T, ? extends R> mapper) {
        mMapper = mapper;
    }

    @Override
    public R process(T input) throws VisionException {
        return mMapper.apply(input);
    }
}
