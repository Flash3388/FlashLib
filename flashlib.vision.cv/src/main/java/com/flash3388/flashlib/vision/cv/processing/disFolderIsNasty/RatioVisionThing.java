package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.StreamMappingProcessor;
import com.jmath.vectors.Vector2;
import org.opencv.core.Rect;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class RatioVisionThing extends VisionSomething<RatioVisionThing.RatioScorable>{
    private final double targetHeightToWidthRatio;
    private final double minScore;

    public RatioVisionThing(VisionProcessingConfig processingConfig, PhysicalVisionConfig physicalConfig, Consumer<StandardVisionResult> resultConsumer, Supplier<Time> syncedTime, double targetHeightToWidthRatio, double minScore) {
        super(processingConfig, physicalConfig, resultConsumer, syncedTime);
        this.targetHeightToWidthRatio = targetHeightToWidthRatio;
        this.minScore = minScore;
    }

    @Override
    protected StreamMappingProcessor<Rect, RatioScorable> mappingProcessor(double minContourSize) {
        return new StreamMappingProcessor<>(
                rect -> new RatioScorable(rect, targetHeightToWidthRatio),
                ratioScorable -> ratioScorable.score() > minScore
        );
    }

    protected static class RatioScorable implements Scorable {
        private final Rect mRect;
        private final double mExpectedRatio;

        RatioScorable(Rect rect, double expectedRatio) {
            mRect = rect;
            mExpectedRatio = expectedRatio;
        }

        @Override
        public double getWidth() {
            return mRect.width;
        }

        @Override
        public double getHeight() {
            return mRect.height;
        }

        @Override
        public Vector2 getCenter() {
            return new Vector2(mRect.x + mRect.width * 0.5, mRect.y + mRect.height * 0.5);
        }

        @Override
        public double score() {
            double ratio = mRect.height / (double) mRect.width;
            return ratio > mExpectedRatio ? mExpectedRatio / ratio : ratio / mExpectedRatio;
        }
    }
}
