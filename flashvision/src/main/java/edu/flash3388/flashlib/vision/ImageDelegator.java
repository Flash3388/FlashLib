package edu.flash3388.flashlib.vision;

import java.util.List;
import java.util.Optional;

public class ImageDelegator<T extends Image> {

    private final ImageSource<T> mImageSource;
    private final List<ImagePipeline<T>> mImagePipelines;

    public ImageDelegator(ImageSource<T> imageSource, List<ImagePipeline<T>> imagePipelines) {
        mImageSource = imageSource;
        mImagePipelines = imagePipelines;
    }

    public ImageDelegator addPipeline(ImagePipeline<T> pipeline) {
        mImagePipelines.add(pipeline);
        return this;
    }

    public boolean tryDelegate() {
        Optional<T> optionalImage = mImageSource.get();
        if (!optionalImage.isPresent()) {
            return false;
        }

        T image = optionalImage.get();

        for (ImagePipeline<T> pipeline : mImagePipelines) {
            pipeline.process(image);
        }

        return true;
    }
}
