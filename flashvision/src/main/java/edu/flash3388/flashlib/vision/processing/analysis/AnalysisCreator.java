package edu.flash3388.flashlib.vision.processing.analysis;

import edu.flash3388.flashlib.vision.Image;

import java.util.Optional;

public interface AnalysisCreator<T extends Image> {

    Optional<Analysis> tryCreateAnalysis(T image);
}
