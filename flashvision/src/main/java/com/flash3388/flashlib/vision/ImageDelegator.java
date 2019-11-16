package com.flash3388.flashlib.vision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ImageDelegator<T extends Image> {

    private final ImageSource<T> mImageSource;
    private final Collection<ImagePipeline<T>> mImagePipelines;

    public ImageDelegator(ImageSource<T> imageSource, Collection<ImagePipeline<T>> imagePipelines) {
        mImageSource = imageSource;
        mImagePipelines = new ArrayList<>(imagePipelines);
    }

    public ImageDelegator(ImageSource<T> imageSource, ImagePipeline<T>... imagePipelines) {
        this(imageSource, Arrays.asList(imagePipelines));
    }

    public void delegate() throws VisionException {
        T image = mImageSource.get();

        for (ImagePipeline<T> pipeline : mImagePipelines) {
            pipeline.process(image);
        }
    }
}
