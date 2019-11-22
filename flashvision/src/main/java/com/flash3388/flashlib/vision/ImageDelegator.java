package com.flash3388.flashlib.vision;

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

    public ImageDelegator(ImageSource<? extends T> imageSource, ImagePipeline<? super T>... imagePipelines) {
        this(imageSource, Arrays.asList(imagePipelines));
    }

    public void delegate() throws VisionException {
        T image = mImageSource.get();

        for (ImagePipeline<? super T> pipeline : mImagePipelines) {
            pipeline.process(image);
        }
    }
}
