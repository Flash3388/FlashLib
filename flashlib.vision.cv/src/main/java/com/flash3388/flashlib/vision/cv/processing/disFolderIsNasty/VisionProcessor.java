package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

import java.util.concurrent.Future;

public interface VisionProcessor {
    Future<?> startVisionPipeline(double minContourSize);
    VisionProcessingConfig getProcessingConfig();
    PhysicalVisionConfig getPhysicalConfig();

    default void shutdownVisionPipeline() {
        getProcessingConfig().getExecutorService().shutdownNow();
    }
}
