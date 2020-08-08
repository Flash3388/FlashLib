package com.flash3388.flashlib.vision;

import com.castle.concurrent.service.PeriodicTaskService;
import com.castle.concurrent.service.Service;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ImageDelegator<T extends Image> {

    private final ImageSource<? extends T> mImageSource;
    private final Collection<ImagePipeline<? super T>> mImagePipelines;

    public ImageDelegator(ImageSource<? extends T> imageSource, Collection<ImagePipeline<? super T>> imagePipelines) {
        mImageSource = imageSource;
        mImagePipelines = new ArrayList<>(imagePipelines);
    }

    @SafeVarargs
    public ImageDelegator(ImageSource<? extends T> imageSource, ImagePipeline<? super T>... imagePipelines) {
        this(imageSource, Arrays.asList(imagePipelines));
    }

    public void delegate() throws VisionException {
        T image = mImageSource.get();

        for (ImagePipeline<? super T> pipeline : mImagePipelines) {
            pipeline.process(image);
        }
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
