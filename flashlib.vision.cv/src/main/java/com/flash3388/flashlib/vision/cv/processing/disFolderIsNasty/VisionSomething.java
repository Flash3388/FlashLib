package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.RectProcessor;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.BestProcessor;
import com.flash3388.flashlib.vision.processing.StreamMappingProcessor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.AnalysisAlgorithms;
import org.opencv.core.Rect;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class VisionSomething<S extends Scorable> implements  VisionProcessor{
    private final VisionProcessingConfig processingConfig;
    private final PhysicalVisionConfig physicalConfig;
    private final Consumer<StandardVisionResult> resultConsumer;
    private final Supplier<Time> syncedTime;

    private final CvProcessing cvProcessing;

    public VisionSomething(VisionProcessingConfig processingConfig, PhysicalVisionConfig physicalConfig, Consumer<StandardVisionResult> resultConsumer, Supplier<Time> syncedTime) {
        this.processingConfig = processingConfig;
        this.physicalConfig = physicalConfig;
        this.resultConsumer = resultConsumer;
        this.syncedTime = syncedTime;

        cvProcessing = new CvProcessing();
    }

    @Override
    public Future<?> startVisionPipeline(double minContourSize) {
        return processingConfig.getImageSource().asyncPoll(processingConfig.getExecutorService(),
                processingConfig.getOutputPipeline().divergeTo(new VisionPipeline.Builder<CvImage, Optional<S>>()
                        .process(new HsvRangeProcessor(processingConfig.getColorSettings(), cvProcessing)
                                .andThen(new RectProcessor(cvProcessing, rect -> rect.area() > minContourSize)
                                        .andThen(mappingProcessor(minContourSize).andThen(new BestProcessor<>(Scorable::compareTo)))))
                        .analyse(this::analyze)
                        .analysisTo(analysis -> resultConsumer.accept(StandardVisionResult.fromAnalysis(analysis)))
                        .build())
                , processingConfig.getHandler());
    }

    @Override
    public VisionProcessingConfig getProcessingConfig() {
        return processingConfig;
    }

    @Override
    public PhysicalVisionConfig getPhysicalConfig() {
        return physicalConfig;
    }

    protected abstract StreamMappingProcessor<Rect, S> mappingProcessor(double minContourSize);

    private Optional<Analysis> analyze(Image image, Optional<S> best) {
        if (!best.isPresent())
            return Optional.empty();
        S scorable = best.get();

        double distance = AnalysisAlgorithms.measureDistance(scorable.getWidth(), image.getWidth(),
                physicalConfig.getTargetWidth(), physicalConfig.getCamFovRadians());
        double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(scorable.getCenter().x(),
                image.getWidth(), physicalConfig.getCamFovRadians());

        return Optional.of(new Analysis.Builder()
                .put("distance", distance)
                .put("angle", angle)
                .put("timestamp", syncedTime.get())
                .build());
    }
}
