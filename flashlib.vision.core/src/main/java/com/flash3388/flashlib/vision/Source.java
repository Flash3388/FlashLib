package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * A source of objects, supplying when {@link #get()} is called.
 *
 * @param <T> type of object supplied.
 *
 * @since FlashLib 3.0.0
 */
public interface Source<T> {

    /**
     * Gets an object from the source.
     *
     * @return object
     * @throws VisionException if the source encounters an error while supplying.
     */
    T get() throws VisionException;

    /**
     * Executes a single poll from this source asynchronously using the given executor service into
     * a pipeline.
     *
     * @param executorService executor service for executing the polling task
     * @param pipeline pipeline to poll into
     * @param throwableHandler handler for any error while polling
     *
     * @return a future controlling the async task
     */
    default Future<?> asyncPoll(ExecutorService executorService,
                                Pipeline<? super T> pipeline,
                                ThrowableHandler throwableHandler) {
        return executorService.submit(new SourcePoller<>(this, pipeline, throwableHandler));
    }

    /**
     * Executes a single poll from this source asynchronously into a pipeline.
     *
     * @param pipeline pipeline to poll into.
     * @param throwableHandler handler for any error while polling.
     */
    default void asyncPoll(Pipeline<? super T> pipeline,
                           ThrowableHandler throwableHandler) {
        Thread thread = new Thread(new SourcePoller<>(this, pipeline, throwableHandler),
                this + "-poller");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Runs an asynchronous periodic polling task which polls from this source into a pipeline at a fixed rate.
     *
     * @param executorService executor service for executing the polling task
     * @param rate rate of polling
     * @param pipeline pipeline to poll into
     * @param throwableHandler handler for any error while polling.
     *
     * @return a future controlling the async task
     */
    default Future<?> asyncPollAtFixedRate(ScheduledExecutorService executorService,
                                           Time rate,
                                           Pipeline<? super T> pipeline,
                                           ThrowableHandler throwableHandler) {
        return executorService.scheduleAtFixedRate(
                new SourcePoller<>(this, pipeline, throwableHandler),
                0, rate.value(), rate.unit());
    }

    /**
     * Runs an asynchronous periodic polling task which polls from this source into a pipeline at a fixed rate.
     *
     * @param rate rate of polling
     * @param pipeline pipeline to poll into
     * @param throwableHandler handler for any error while polling.
     */
    default void asyncPollAtFixedRate(Time rate,
                                      Pipeline<? super T> pipeline,
                                      ThrowableHandler throwableHandler) {
        Thread thread = new Thread(new PeriodicPoller<>(this, rate, pipeline, throwableHandler),
                this + "-poller");
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Wraps this source as a {@link Supplier} object. Calling {@link Supplier#get()} will invoke
     * {@link #get()}. If an exception is thrown, it will be consumed by the given <code>throwableHandler</code>
     * and <code>null</code> will be returned instead.
     *
     * @param throwableHandler handler for any exception thrown by {@link #get()}.
     *
     * @return a supplier wrapper.
     */
    default Supplier<T> asSupplier(ThrowableHandler throwableHandler) {
        return ()-> {
            try {
                return this.get();
            } catch (VisionException e) {
                throwableHandler.handle(e);
                return null;
            }
        };
    }

    /**
     * Creates a new source, supplying the given objects one by one.
     *
     * @param objects objects to be supplied by the source.
     * @param <T> type of objects.
     *
     * @return a new source.
     */
    @SafeVarargs
    static <T> Source<T> of(T... objects) {
        return new QueueSource<T>(objects);
    }

    /**
     * Creates a new source, supplying the given objects one by one.
     *
     * @param objects objects to be supplied by the source.
     * @param <T> type of objects.
     *
     * @return a new source.
     */
    static <T> Source<T> of(Collection<T> objects) {
        return new QueueSource<T>(new ArrayDeque<>(objects));
    }
}
