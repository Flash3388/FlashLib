package com.flash3388.flashlib.vision.detection;


import com.flash3388.flashlib.vision.Image;

import java.util.Collection;

public interface ObjectDetector<I extends Image, T extends Target> {

    Collection<? extends T> detect(I image);
}
