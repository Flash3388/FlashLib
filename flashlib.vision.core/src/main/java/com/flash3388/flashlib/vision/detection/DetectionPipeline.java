package com.flash3388.flashlib.vision.detection;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

import java.util.Collection;
import java.util.Map;

public class DetectionPipeline<I extends Image, T extends Target> implements Pipeline<I> {

    private final ObjectDetector<I, T> mDetector;
    private final ObjectTracker<T> mTracker;
    private final Pipeline<Map<Integer, ? extends T>> mOutPipe;

    public DetectionPipeline(ObjectDetector<I, T> detector, ObjectTracker<T> tracker, Pipeline<Map<Integer, ? extends T>> outPipe) {
        mDetector = detector;
        mTracker = tracker;
        mOutPipe = outPipe;
    }

    @Override
    public void process(I input) throws VisionException {
        Collection<? extends T> objects = mDetector.detect(input);
        Map<Integer, ? extends T> targets = mTracker.updateTracked(objects);

        mOutPipe.process(targets);
    }
}
