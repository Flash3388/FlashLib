package com.flash3388.flashlib.vision.processing.analysis;

import com.flash3388.flashlib.vision.Image;

import java.util.Optional;

@FunctionalInterface
public interface ImageAnalyser<T extends Image> {

    Optional<Analysis> analyse(T image) throws ImageAnalysingException;
}
