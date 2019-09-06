package com.flash3388.flashlib.vision;

import java.util.Arrays;
import java.util.List;

public class ImageDelegator<T extends Image> {

    private final ImageSource<T> mImageSource;
    private final List<ImagePipeline<T>> mImagePipelines;

    public ImageDelegator(ImageSource<T> imageSource, List<ImagePipeline<T>> imagePipelines) {
        mImageSource = imageSource;
        mImagePipelines = imagePipelines;
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
