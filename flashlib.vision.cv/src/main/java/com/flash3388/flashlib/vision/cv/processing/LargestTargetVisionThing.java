package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.processing.StreamMappingProcessor;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.jmath.vectors.Vector2;
import org.opencv.core.Rect;

import java.util.function.Consumer;

public class LargestTargetVisionThing extends VisionSomething<LargestTargetVisionThing.TargetScorable> {
    public LargestTargetVisionThing(VisionProcessingConfig processingConfig, PhysicalVisionConfig physicalConfig, Consumer<Analysis> analysisConsumer) {
        super(processingConfig, physicalConfig, analysisConsumer);
    }

    @Override
    protected StreamMappingProcessor<Rect, TargetScorable> mappingProcessor(double minContourSize) {
        return new StreamMappingProcessor<>(TargetScorable::new, targetScorable -> targetScorable.score() < minContourSize);
    }

    protected static class TargetScorable implements Scorable {
        private final Rect rect;

        TargetScorable(Rect rect) {
            this.rect = rect;
        }

        @Override
        public double getWidth() {
            return rect.width;
        }

        @Override
        public double getHeight() {
            return rect.height;
        }

        @Override
        public Vector2 getCenter() {
            return new Vector2(rect.x + rect.width * 0.5, rect.y + rect.height * 0.5);
        }

        @Override
        public double score() {
            return rect.area();
        }
    }
}
