package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.time.Time;
import edu.flash3388.flashlib.util.flow.SingleUseRunner;
import edu.flash3388.flashlib.vision.exceptions.VisionException;

import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    protected void startController() {
        mExecutorService.scheduleAtFixedRate(
                new ImageDelegationTask<>(mImageDelegator, mLogger),
                mDelegationPeriod.getValue(),
                mDelegationPeriod.getValue(),
                mDelegationPeriod.getUnit());
    }

    @Override
    protected void stopController() {
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
                mLogger.log(Level.SEVERE, "error in image delegation", e);
            }
        }
    }
}
