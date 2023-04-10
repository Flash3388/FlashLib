package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

public class SourcePollTask<T> extends SourceSinglePollTask<T> {

    private final Time mProcessTime;

    public SourcePollTask(Source<? extends T> source,
                          Pipeline<? super T> pipeline,
                          SourcePollingObserver observer,
                          Clock clock,
                          Time processTime) {
        super(source, pipeline, observer, clock);
        mProcessTime = processTime;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Time runTime = runOnce();

                if (runTime.before(mProcessTime)) {
                    Time sleepTime = mProcessTime.sub(runTime);
                    LOGGER.debug("polling process finished faster then expected, suspending for {}",
                            sleepTime.valueAsMillis());

                    //noinspection BusyWait
                    Thread.sleep(sleepTime.valueAsMillis());
                }
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
