package com.flash3388.flashlib.vision;

import org.slf4j.Logger;

public class ImageDelegationTask<T extends Image> implements Runnable {

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
