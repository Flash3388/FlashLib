package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.vision.processing.ImageProcessingException;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface ImagePipeline<T extends Image> {

    void process(T image) throws ImageProcessingException;

    default ImagePipeline<T> divergeTo(ImagePipeline<? super T> pipeline) {
        return new SyncDivergingPipeline<>(Arrays.asList(this, pipeline));
    }

    class SyncDivergingPipeline<T extends Image> implements ImagePipeline<T> {

        private final Collection<ImagePipeline<? super T>> mImagePipelines;

        private SyncDivergingPipeline(Collection<ImagePipeline<? super T>> imagePipelines) {
            mImagePipelines = imagePipelines;
        }

        @Override
        public void process(T image) throws ImageProcessingException {
            for (ImagePipeline<? super T> pipeline : mImagePipelines) {
                pipeline.process(image);
            }
        }
    }
}
