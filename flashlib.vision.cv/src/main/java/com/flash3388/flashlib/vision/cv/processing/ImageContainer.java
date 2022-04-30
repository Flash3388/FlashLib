package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.processing.color.ColorSpace;

public interface ImageContainer {

    ColorSpace getColorSpace();
    CvImage getImage();
}
