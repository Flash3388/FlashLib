package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.processing.StreamMappingProcessor;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.jmath.vectors.Vector2;
import org.opencv.core.Rect;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class LargestTargetVisionThing extends VisionSomething<LargestTargetVisionThing.RectSizeScorable> {
    public LargestTargetVisionThing(VisionProcessingConfig processingConfig, PhysicalVisionConfig physicalConfig, Consumer<StandardVisionResult> resultConsumer, Supplier<Time> syncedTime) {
        super(processingConfig, physicalConfig, resultConsumer, syncedTime);
    }

    @Override
    protected StreamMappingProcessor<Rect, RectSizeScorable> mappingProcessor(double minContourSize) {
        return new StreamMappingProcessor<>(RectSizeScorable::new, rectSizeScorable -> rectSizeScorable.score() < minContourSize);
    }

    protected static class RectSizeScorable implements Scorable {
        private final Rect rect;

        RectSizeScorable(Rect rect) {
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
