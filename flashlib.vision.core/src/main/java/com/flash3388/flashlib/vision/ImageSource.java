package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public interface ImageSource<T extends Image> {

    T get() throws VisionException;

    default Future<?> asyncPoll(ExecutorService executorService,
                                ImagePipeline<? super T> pipeline,
                                ThrowableHandler throwableHandler) {
        return executorService.submit(new ImagePoller<>(this, pipeline, throwableHandler));
    }

    default Future<?> asyncPollAtFixedRate(ScheduledExecutorService executorService,
                                           Time rate,
                                           ImagePipeline<? super T> pipeline,
                                           ThrowableHandler throwableHandler) {
        return executorService.scheduleAtFixedRate(
                new ImagePoller<>(this, pipeline, throwableHandler),
                rate.value(), rate.value(), rate.unit());
    }

    static <T extends Image> ImageSource<T> of(T... images) {
        return new ImageQueue<T>(images);
    }
}
