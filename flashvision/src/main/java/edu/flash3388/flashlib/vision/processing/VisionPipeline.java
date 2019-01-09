package edu.flash3388.flashlib.vision.processing;

import edu.flash3388.flashlib.vision.Image;
import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.vision.processing.analysis.Analysis;
import edu.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import edu.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.List;
import java.util.function.Consumer;

public class VisionPipeline<T extends Image> implements ImagePipeline<T> {

    private final List<ImageProcessor<T>> mImageProcessors;
    private final ImageAnalyser<T> mImageAnalyser;
    private final Consumer<Analysis> mAnalysisConsumer;

    public VisionPipeline(List<ImageProcessor<T>> imageProcessors, ImageAnalyser<T> imageAnalyser, Consumer<Analysis> analysisConsumer) {
        mImageProcessors = imageProcessors;
        mImageAnalyser = imageAnalyser;
        mAnalysisConsumer = analysisConsumer;
    }

    public VisionPipeline<T> addProcessor(ImageProcessor<T> imageProcessor) {
        mImageProcessors.add(imageProcessor);
        return this;
    }

    @Override
    public void process(T image) throws ImageProcessingException {
        try {
            for (ImageProcessor<T> processor : mImageProcessors) {
                image = processor.process(image);
            }

            Analysis analysis = mImageAnalyser.analyse(image);
            mAnalysisConsumer.accept(analysis);
        } catch (ImageAnalysingException e) {
            throw new ImageProcessingException(e);
        }
    }
}
