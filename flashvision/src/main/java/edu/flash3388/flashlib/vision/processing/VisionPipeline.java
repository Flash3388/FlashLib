package edu.flash3388.flashlib.vision.processing;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.vision.processing.analysis.Analysis;
import edu.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VisionPipeline<T extends Image> implements ImagePipeline<T> {

    private final List<ImageProcessor<T>> mImageProcessors;
    private final ImageAnalyser<T> mImageAnalyser;
    private final Consumer<Analysis> mAnalysisConsumer;
    private final Logger mLogger;

    public VisionPipeline(List<ImageProcessor<T>> imageProcessors, ImageAnalyser<T> imageAnalyser, Consumer<Analysis> analysisConsumer, Logger logger) {
        mImageProcessors = imageProcessors;
        mImageAnalyser = imageAnalyser;
        mAnalysisConsumer = analysisConsumer;
        mLogger = logger;
    }

    public VisionPipeline<T> addProcessor(ImageProcessor<T> imageProcessor) {
        mImageProcessors.add(imageProcessor);
        return this;
    }

    @Override
    public void process(T image) {
        try {
            for (ImageProcessor<T> processor : mImageProcessors) {
                image = processor.process(image);
            }

            Optional<Analysis> optionalAnalysis = mImageAnalyser.tryAnalyse(image);
            if (optionalAnalysis.isPresent()) {
                mAnalysisConsumer.accept(optionalAnalysis.get());
            } else {
                mLogger.log(Level.WARNING, "Analysis creator - no analysis could be created");
            }
        } catch (ImageProcessingException e) {
            mLogger.log(Level.SEVERE, "failed to process image", e);
        }
    }
}
