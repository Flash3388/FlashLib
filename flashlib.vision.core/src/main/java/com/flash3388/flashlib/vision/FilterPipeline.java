package com.flash3388.flashlib.vision;

import java.util.function.Predicate;

public class FilterPipeline<T> implements Pipeline<T> {

    private final Predicate<? super T> mFilter;
    private final Pipeline<? super T> mNext;

    public FilterPipeline(Predicate<? super T> filter, Pipeline<? super T> next) {
        mFilter = filter;
        mNext = next;
    }

    @Override
    public void process(T input) throws VisionException {
        if (mFilter.test(input)) {
            mNext.process(input);
        }
    }

}
