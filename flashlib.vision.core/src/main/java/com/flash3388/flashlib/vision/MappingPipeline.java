package com.flash3388.flashlib.vision;

import java.util.function.Function;

public class MappingPipeline<T, T2> implements Pipeline<T> {

    private final Pipeline<? super T2> mNext;
    private final Function<? super T, ? extends T2> mMapper;

    public MappingPipeline(Pipeline<? super T2> next, Function<? super T, ? extends T2> mapper) {
        mNext = next;
        mMapper = mapper;
    }

    @Override
    public void process(T input) throws VisionException {
        T2 next = mMapper.apply(input);
        mNext.process(next);
    }
}
