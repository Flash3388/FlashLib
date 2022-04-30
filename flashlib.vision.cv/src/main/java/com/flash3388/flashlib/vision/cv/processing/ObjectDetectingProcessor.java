package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.processing.Processor;
import org.opencv.core.MatOfPoint;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ObjectDetectingProcessor implements Processor<ImageContainer, Map<Integer, ? extends Scorable>> {

    private final ObjectDetector mObjectDetector;
    private final ObjectTracker mObjectTracker;
    private final Function<MatOfPoint, ? extends Scorable> mContourMaker;

    public ObjectDetectingProcessor(ObjectDetector objectDetector, ObjectTracker objectTracker, Function<MatOfPoint, ? extends Scorable> contourMaker) {
        mObjectDetector = objectDetector;
        mObjectTracker = objectTracker;
        mContourMaker = contourMaker;
    }

    @Override
    public Map<Integer, ? extends Scorable> process(ImageContainer input) throws VisionException {
        Collection<? extends Scorable> objects = mObjectDetector.detect(input.getImage(), mContourMaker);
        return mObjectTracker.updateTracked(objects);
    }
}
