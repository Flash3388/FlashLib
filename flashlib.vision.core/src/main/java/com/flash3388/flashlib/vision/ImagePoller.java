package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

public class ImagePoller<T extends Image> implements Runnable {

    private final ImageSource<? extends T> mImageSource;
    private final ImagePipeline<? super T> mImagePipeline;
    private final ThrowableHandler mThrowableHandler;

    public ImagePoller(ImageSource<? extends T> imageSource,
                       ImagePipeline<? super T> imagePipeline,
                       ThrowableHandler throwableHandler) {
        mImageSource = imageSource;
        mImagePipeline = imagePipeline;
        mThrowableHandler = throwableHandler;
    }

    @Override
    public void run() {
        try {
            T image = mImageSource.get();
            mImagePipeline.process(image);
        } catch (Throwable t) {
            mThrowableHandler.handle(t);
        }
    }
}
