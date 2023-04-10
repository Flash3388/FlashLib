package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class SourceSinglePollTask<T> implements Runnable {

    protected static final Logger LOGGER = Logging.getLogger("Vision", "SourcePollTask");

    private final Source<? extends T> mSource;
    private final Pipeline<? super T> mPipeline;
    private final SourcePollingObserver mObserver;
    private final Clock mClock;

    public SourceSinglePollTask(Source<? extends T> source,
                                Pipeline<? super T> pipeline,
                                SourcePollingObserver observer,
                                Clock clock) {
        mSource = source;
        mPipeline = pipeline;
        mObserver = observer;
        mClock = clock;
    }

    @Override
    public void run() {
        runOnce();
    }

    protected final Time runOnce() {
        Time startTime = mClock.currentTime();

        LOGGER.trace("Starting polling process");
        mObserver.onStartProcess();
        try {
            T data = mSource.get();
            mPipeline.process(data);
        } catch (Throwable t) {
            LOGGER.debug("Polling process encountered an error", t);
            mObserver.onErroredProcess(t);
        }

        Time endTime = mClock.currentTime();
        Time runTime = endTime.sub(startTime);

        LOGGER.trace("Finished iteration of polling process. runtime={}", runTime.valueAsMillis());
        mObserver.onEndProcess(runTime);

        return runTime;
    }
}
