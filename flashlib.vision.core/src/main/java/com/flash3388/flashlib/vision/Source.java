package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Clock;
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
     * @param observer observer of the processing run, or null
     * @param clock clock
     * @param processTime time expected for a single run. If a single process takes less, it will
     *                    suspend for the remainder of the time frame. Can be used for FPS
     *                    timing.
     *
     * @return a future controlling the async task
     */
    default Future<?> asyncPoll(ExecutorService executorService,
                                Pipeline<? super T> pipeline,
                                SourcePollingObserver observer,
                                Clock clock,
                                Time processTime) {
        return executorService.submit(new SourcePollTask<>(this, pipeline,
                observer, clock, processTime));
    }

    /**
     * Executes a single poll from this source asynchronously into a pipeline.
     *
     * @param pipeline pipeline to poll into.
     * @param observer observer of the processing run, or null
     * @param clock clock
     * @param processTime time expected for a single run. If a single process takes less, it will
     *                    suspend for the remainder of the time frame. Can be used for FPS
     *                    timing.
     *
     * @return the running thread
     */
    default Thread asyncPoll(Pipeline<? super T> pipeline,
                             SourcePollingObserver observer,
                             Clock clock,
                             Time processTime) {
        Thread thread = new Thread(new SourcePollTask<>(this, pipeline, observer, clock, processTime),
                this + "-poller");
        thread.setDaemon(true);
        thread.start();

        return thread;
    }

    /**
     * Runs an asynchronous periodic polling task which polls from this source into a pipeline at a fixed rate.
     *
     * @param executorService executor service for executing the polling task
     * @param rate rate of polling
     * @param pipeline pipeline to poll into
     * @param observer observer of the processing run, or null
     * @param clock clock
     *
     * @return a future controlling the async task
     */
    default Future<?> asyncPollAtFixedRate(ScheduledExecutorService executorService,
                                           Time rate,
                                           Pipeline<? super T> pipeline,
                                           SourcePollingObserver observer,
                                           Clock clock) {
        return executorService.scheduleAtFixedRate(
                new SourceSinglePollTask<>(this, pipeline, observer, clock),
                0, rate.value(), rate.unit());
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
        return new QueueSource<>(objects);
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
        return new QueueSource<>(new ArrayDeque<>(objects));
    }
}
