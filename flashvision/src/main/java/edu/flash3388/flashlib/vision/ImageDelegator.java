package edu.flash3388.flashlib.vision;

import java.util.List;
import java.util.Optional;

public class ImageDelegator {

    private final ImageSource mImageSource;
    private final List<ImagePipeline> mImagePipelines;

    public ImageDelegator(ImageSource imageSource, List<ImagePipeline> imagePipelines) {
        mImageSource = imageSource;
        mImagePipelines = imagePipelines;
    }

    public ImageDelegator addPipeline(ImagePipeline pipeline) {
        mImagePipelines.add(pipeline);
        return this;
    }

    public boolean tryDelegate() {
        Optional<Image> optionalImage = mImageSource.get();
        if (!optionalImage.isPresent()) {
            return false;
        }

        Image image = optionalImage.get();

        for (ImagePipeline pipeline : mImagePipelines) {
            pipeline.process(image);
        }

        return true;
    }
}
