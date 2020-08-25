package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public interface ImageSource<T extends Image> {

    T get() throws VisionException;

    default Future<?> asyncPoll(ExecutorService executorService,
                                Pipeline<? super T> pipeline,
                                ThrowableHandler throwableHandler) {
        return executorService.submit(new ImagePoller<>(this, pipeline, throwableHandler));
    }

    default Future<?> asyncPollAtFixedRate(ScheduledExecutorService executorService,
                                           Time rate,
                                           Pipeline<? super T> pipeline,
                                           ThrowableHandler throwableHandler) {
        return executorService.scheduleAtFixedRate(
                new ImagePoller<>(this, pipeline, throwableHandler),
                0, rate.value(), rate.unit());
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

    static <T extends Image> ImageSource<T> of(T... images) {
        return new ImageQueue<T>(images);
    }

    static <T extends Image> ImageSource<T> of(Collection<T> images) {
        return new ImageQueue<T>(new ArrayDeque<>(images));
    }
}
