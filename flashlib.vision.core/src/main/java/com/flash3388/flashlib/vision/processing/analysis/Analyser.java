package com.flash3388.flashlib.vision.processing.analysis;

import java.util.Optional;

@FunctionalInterface
public interface Analyser<T, T2> {

    Optional<Analysis> analyse(T originalInput, T2 postProcess) throws ImageAnalysingException;
}
