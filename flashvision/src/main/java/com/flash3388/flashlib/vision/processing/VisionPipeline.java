package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalyser;
import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.ImagePipeline;
import com.flash3388.flashlib.vision.processing.analysis.ImageAnalysingException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class VisionPipeline<T extends Image> implements ImagePipeline<T> {

    private final ImageAnalyser<T> mImageAnalyser;
    private final Consumer<Analysis> mAnalysisConsumer;
    private final List<ImageProcessor<T>> mImageProcessors;

    public VisionPipeline(ImageAnalyser<T> imageAnalyser, Consumer<Analysis> analysisConsumer, List<ImageProcessor<T>> imageProcessors) {
        mImageAnalyser = imageAnalyser;
        mAnalysisConsumer = analysisConsumer;
        mImageProcessors = imageProcessors;
    }

    public VisionPipeline(ImageAnalyser<T> imageAnalyser, Consumer<Analysis> analysisConsumer, ImageProcessor<T> ... imageProcessors) {
        this(imageAnalyser, analysisConsumer, Arrays.asList(imageProcessors));
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
