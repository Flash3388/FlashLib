package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.processing.color.ColorSpace;

public class ProcessedImageContainer implements ImageContainer {

    private final CvImage mImage;
    private final ColorSpace mColorSpace;
    private final ImageContainer mOriginalContainer;

    public ProcessedImageContainer(CvImage image, ColorSpace colorSpace, ImageContainer originalContainer) {
        mImage = image;
        mColorSpace = colorSpace;
        mOriginalContainer = originalContainer;
    }

    @Override
    public ColorSpace getColorSpace() {
        return mColorSpace;
    }

    @Override
    public CvImage getImage() {
        return mImage;
    }

    public ImageContainer getOriginalContainer() {
        return mOriginalContainer;
    }
}
