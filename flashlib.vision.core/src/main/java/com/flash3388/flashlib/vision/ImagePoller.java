package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

public class ImagePoller<T extends Image> implements Runnable {

    private final ImageSource<? extends T> mImageSource;
    private final Pipeline<? super T> mPipeline;
    private final ThrowableHandler mThrowableHandler;

    public ImagePoller(ImageSource<? extends T> imageSource,
                       Pipeline<? super T> pipeline,
                       ThrowableHandler throwableHandler) {
        mImageSource = imageSource;
        mPipeline = pipeline;
        mThrowableHandler = throwableHandler;
    }

    @Override
    public void run() {
        try {
            T image = mImageSource.get();
            mPipeline.process(image);
        } catch (Throwable t) {
            mThrowableHandler.handle(t);
        }
    }
}
