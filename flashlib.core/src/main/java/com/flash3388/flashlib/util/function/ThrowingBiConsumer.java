package com.flash3388.flashlib.util.function;

@FunctionalInterface
public interface ThrowingBiConsumer<T1, T2, E extends Exception> {

    void consume(T1 t1, T2 t2) throws E;
}
