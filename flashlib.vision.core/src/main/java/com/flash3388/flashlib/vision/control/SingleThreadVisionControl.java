package com.flash3388.flashlib.vision.control;

import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.VisionResult;
import com.flash3388.flashlib.vision.control.event.NewResultEvent;
import com.flash3388.flashlib.vision.control.event.VisionListener;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.notifier.Controllers;
import com.notifier.EventController;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class SingleThreadVisionControl<S> implements VisionControl {

    public static class Builder<S> {

        private final Function<Runnable, Future<?>> mRunner;

        private EventController mEventController;
        private Clock mClock;
        private ThrowableHandler mThrowableHandler;

        private Source<S> mSource;
        private Pipeline<VisionData<S>> mPreProcessPipeline;
        private Processor<VisionData<S>, Optional<Analysis>> mProcessor;

        private Builder(Function<Runnable, Future<?>> runner) {
            mRunner = runner;
            mThrowableHandler = Throwables.silentHandler();
        }

        public Builder<S> eventController(EventController eventController) {
            mEventController = eventController;
            return this;
        }

        public Builder<S> clock(Clock clock) {
            mClock = clock;
            return this;
        }

        public Builder<S> onError(ThrowableHandler handler) {
            mThrowableHandler = handler;
            return this;
        }

        public Builder<S> source(Source<S> source) {
            mSource = source;
            return this;
        }

        public Builder<S> preProcessWithOptions(Pipeline<VisionData<S>> pipeline) {
            mPreProcessPipeline = pipeline;
            return this;
        }

        public Builder<S> preProcess(Pipeline<S> pipeline) {
            mPreProcessPipeline = (data)-> pipeline.process(data.getData());
            return this;
        }

        public Builder<S> processor(Processor<VisionData<S>, Optional<Analysis>> processor) {
            mProcessor = processor;
            return this;
        }

        public SingleThreadVisionControl<S> build() {
            if (mClock == null) {
                mClock = new SystemNanoClock();
            }
            if (mEventController == null) {
                mEventController = Controllers.newSyncExecutionController();
            }

            return new SingleThreadVisionControl<S>(mRunner, mClock, mEventController,
                    mSource,
                    mProcessor.divergeIn(mPreProcessPipeline),
                    mThrowableHandler);
        }
    }

    public static <S> Builder<S> withExecutorService(ScheduledExecutorService executorService, Time pollingTime) {
        Function<Runnable, Future<?>> runner = (task)->
                executorService.scheduleAtFixedRate(task, pollingTime.value(), pollingTime.value(), pollingTime.unit());
        return new Builder<S>(runner);
    }

    private final Function<Runnable, Future<?>> mRunner;
    private final Clock mClock;
    private final EventController mEventController;
    private final VisionOptions mVisionOptions;

    private final Runnable mTask;

    private final AtomicReference<Future<?>> mFuture;
    private final AtomicReference<VisionResult> mLatestResult;

    private SingleThreadVisionControl(Function<Runnable, Future<?>> runner, Clock clock, EventController eventController,
                              Source<S> source, Processor<VisionData<S>, Optional<Analysis>> processor, ThrowableHandler throwableHandler) {
        mRunner = runner;
        mClock = clock;
        mEventController = eventController;
        mVisionOptions = new VisionOptions();

        mFuture = new AtomicReference<>();
        mLatestResult = new AtomicReference<>();

        mTask = new Task<S>(source,
                processor.pipeTo(new NewAnalysisHandler(eventController, clock, mLatestResult)),
                throwableHandler, mVisionOptions);
    }

    @Override
    public boolean isRunning() {
        return mFuture.get() != null;
    }

    @Override
    public void start() {
        Future<?> future = mRunner.apply(mTask);
        mFuture.set(future);
    }

    @Override
    public void stop() {
        Future<?> future = mFuture.get();
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public <T> void setOption(VisionOption<T> option, T value) {
        mVisionOptions.put(option, value);
    }

    @Override
    public <T> Optional<T> getOption(VisionOption<T> option) {
        return mVisionOptions.get(option);
    }

    @Override
    public <T> T getOptionOrDefault(VisionOption<T> option, T defaultValue) {
        return mVisionOptions.getOrDefault(option, defaultValue);
    }

    @Override
    public Optional<VisionResult> getLatestResult() {
        return getLatestResult(false);
    }

    @Override
    public Optional<VisionResult> getLatestResult(boolean clear) {
        return Optional.ofNullable(clear ? mLatestResult.getAndSet(null) : mLatestResult.get());
    }

    @Override
    public Optional<VisionResult> getLatestResult(Time maxTimestamp) {
        return getLatestResult(maxTimestamp, false);
    }

    @Override
    public Optional<VisionResult> getLatestResult(Time maxTimestamp, boolean clear) {
        VisionResult result = clear ? mLatestResult.getAndSet(null) : mLatestResult.get();
        if (result == null) {
            return Optional.empty();
        }

        Time now = mClock.currentTime();
        Time passed = now.sub(result.getTimestamp());
        if (passed.after(maxTimestamp)) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    @Override
    public void addListener(VisionListener listener) {
        mEventController.registerListener(listener);
    }

    private static class Task<S> implements Runnable {

        private final Source<S> mSource;
        private final Pipeline<VisionData<S>> mPipeline;
        private final ThrowableHandler mThrowableHandler;
        private final VisionOptions mVisionOptions;

        private Task(Source<S> source, Pipeline<VisionData<S>> pipeline,
                     ThrowableHandler throwableHandler, VisionOptions visionOptions) {
            mSource = source;
            mPipeline = pipeline;
            mThrowableHandler = throwableHandler;
            mVisionOptions = visionOptions;
        }

        @Override
        public void run() {
            try {
                S source = mSource.get();
                mPipeline.process(new VisionData<>(source, mVisionOptions));
            } catch (VisionException e) {
                mThrowableHandler.handle(e);
            }
        }
    }

    private static class NewAnalysisHandler implements Pipeline<Optional<Analysis>> {

        private final EventController mEventController;
        private final Clock mClock;
        private final AtomicReference<VisionResult> mVisionResult;

        private NewAnalysisHandler(EventController eventController, Clock clock,
                                   AtomicReference<VisionResult> visionResult) {
            mEventController = eventController;
            mClock = clock;
            mVisionResult = visionResult;
        }

        @Override
        public void process(Optional<Analysis> input) throws VisionException {
            if (!input.isPresent()) {
                return;
            }

            Analysis analysis = input.get();

            Time now = mClock.currentTime();
            VisionResult visionResult = new VisionResult(analysis, now);
            mVisionResult.set(visionResult);

            mEventController.fire(new NewResultEvent(visionResult), NewResultEvent.class,
                    VisionListener.class, VisionListener::onNewResult);
        }
    }
}
