package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public interface Source<T> {

    T get() throws VisionException;

    default Future<?> asyncPoll(ExecutorService executorService,
                                Pipeline<? super T> pipeline,
                                ThrowableHandler throwableHandler) {
        return executorService.submit(new SourcePoller<>(this, pipeline, throwableHandler));
    }

    default void asyncPoll(Pipeline<? super T> pipeline,
                           ThrowableHandler throwableHandler) {
        Thread thread = new Thread(new SourcePoller<>(this, pipeline, throwableHandler),
                toString() + "-poller");
        thread.setDaemon(true);
        thread.start();
    }

    default Future<?> asyncPollAtFixedRate(ScheduledExecutorService executorService,
                                           Time rate,
                                           Pipeline<? super T> pipeline,
                                           ThrowableHandler throwableHandler) {
        return executorService.scheduleAtFixedRate(
                new SourcePoller<>(this, pipeline, throwableHandler),
                0, rate.value(), rate.unit());
    }

    default void asyncPollAtFixedRate(Time rate,
                                      Pipeline<? super T> pipeline,
                                      ThrowableHandler throwableHandler) {
        Thread thread = new Thread(new PeriodicPoller<>(this, rate, pipeline, throwableHandler),
                toString() + "-poller");
        thread.setDaemon(true);
        thread.start();
    }

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

    @SafeVarargs
    static <T extends Image> Source<T> of(T... images) {
        return new QueueSource<T>(images);
    }

    static <T extends Image> Source<T> of(Collection<T> images) {
        return new QueueSource<T>(new ArrayDeque<>(images));
    }
}
