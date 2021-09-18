package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A pipeline for consuming and handling objects.
 *
 * @param <T> type of objects handled by the pipeline.
 *
 * @since FlashLib 3.0.0
 */
@FunctionalInterface
public interface Pipeline<T> {

    /**
     * Processes the given object.
     *
     * @param input object to process
     * @throws VisionException if an error was encountered while processing the object.
     */
    void process(T input) throws VisionException;

    /**
     * Diverges the given object to be consumed by the given pipeline as well as this one.
     *
     * @param pipeline pipeline to share object with.
     *
     * @return a new pipeline. When {@link Pipeline#process(Object)} is called, the object will be passed to be
     *      processed by both this and the given pipeline.
     *      If any pipeline fails during the processing, other pipelines may not get a chance to process it.
     *      Modification of the object may affect processing by some pipelines.
     */
    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncPipeJunction<>(this, pipeline);
    }

    /**
     * Diverges the given object to be consumed by the given pipeline as well as this one.
     * The new pipeline will receive a mapped object.
     *
     * @param pipeline pipeline to share object with.
     * @param mapper a mapper to map the object into a new one to be passed to the new pipeline.
     * @param <T2> type of object expected by the pipeline.
     *
     * @return a new pipeline. When {@link Pipeline#process(Object)} is called, the object will be mapped and
     *      passed to be processed by both this and the given pipeline.
     *      If any pipeline fails during the processing, other pipelines may not get a chance to process it.
     *      Modification of the object may affect processing by some pipelines.
     */
    default <T2> Pipeline<T> mapTo(Pipeline<? super T2> pipeline, Function<? super T, ? extends T2> mapper) {
        return divergeTo(new MappingPipeline<>(pipeline, mapper));
    }

    /**
     * Diverges the given object to be consumed by the given pipeline as well as this one. A filter is placed
     * before the new pipeline, this filter may prevent the object from reaching the pipeline, if the object
     * does not pass the filter.
     *
     * @param filter filter placed before the pipeline.
     * @param pipeline pipeline to share object with.
     *
     * @return a new pipeline. When {@link Pipeline#process(Object)} is called, the object will be tested
     *      against a filter and then passed to be the given pipeline, if it passed the filter. The current
     *      pipeline will receive the object regardless of the result fo the filter.
     *      If any pipeline fails during the processing, other pipelines may not get a chance to process it.
     *      Modification of the object may affect processing by some pipelines.
     */
    default Pipeline<T> filterTo(Predicate<? super T> filter, Pipeline<? super T> pipeline) {
        return divergeTo(new FilterPipeline<>(filter, pipeline));
    }

    /**
     * Wraps this pipeline as a {@link Consumer}. Calling {@link Consumer#accept(Object)} will pass the object
     * to {@link #process(Object)}.
     *
     * @param throwableHandler handler for any exception thrown by {@link #process(Object)}.
     *
     * @return a new consumer wrapper.
     */
    default Consumer<T> asConsumer(ThrowableHandler throwableHandler) {
        return (input)-> {
            try {
                this.process(input);
            } catch (VisionException e) {
                throwableHandler.handle(e);
            }
        };
    }

    static <T, T2> Pipeline<T> mapper(Pipeline<? super T2> pipeline, Function<? super T, ? extends T2> mapper) {
        return new MappingPipeline<>(pipeline, mapper);
    }
}
