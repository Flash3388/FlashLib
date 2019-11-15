package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.flow.SingleUseRunner;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ImageDelegationRunner<T extends Image> extends SingleUseRunner {

    private final ScheduledExecutorService mExecutorService;
    private final ImageDelegator<T> mImageDelegator;
    private final Time mDelegationPeriod;
    private final Logger mLogger;

    public ImageDelegationRunner(ScheduledExecutorService executorService, ImageDelegator<T> imageDelegator, Time delegationPeriod, Logger logger) {
        mExecutorService = executorService;
        mImageDelegator = imageDelegator;
        mDelegationPeriod = delegationPeriod;
        mLogger = logger;
    }

    public ImageDelegationRunner(ImageDelegator<T> imageDelegator, Time delegationPeriod, Logger logger) {
        this(Executors.newSingleThreadScheduledExecutor(), imageDelegator, delegationPeriod, logger);
    }

    @Override
    protected void startRunner() {
        mExecutorService.scheduleAtFixedRate(
                new ImageDelegationTask<>(mImageDelegator, mLogger),
                mDelegationPeriod.value(),
                mDelegationPeriod.value(),
                mDelegationPeriod.unit());
    }

    @Override
    protected void stopRunner() {
        mExecutorService.shutdownNow();
    }

    private static class ImageDelegationTask<T extends Image> implements Runnable {

        private final ImageDelegator<T> mImageDelegator;
        private final Logger mLogger;

        private ImageDelegationTask(ImageDelegator<T> imageDelegator, Logger logger) {
            mImageDelegator = imageDelegator;
            mLogger = logger;
        }

        @Override
        public void run() {
            try {
                mImageDelegator.delegate();
            } catch (VisionException e) {
                mLogger.error("error in image delegation", e);
            }
        }
    }
}
