package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class VisionProcessingConfig {
    private final Source<CvImage> imageSource;
    private final Pipeline<CvImage> outputPipeline;
    private final ScheduledExecutorService executorService;
    private final Supplier<HsvColorSettings> colorSettingsSupplier;
    private final ThrowableHandler handler;

    public VisionProcessingConfig(Source<CvImage> imageSource, Pipeline<CvImage> outputPipeline, ScheduledExecutorService executorService, Supplier<HsvColorSettings> colorSettingsSupplier, ThrowableHandler handler) {
        this.imageSource = imageSource;
        this.outputPipeline = outputPipeline;
        this.executorService = executorService;
        this.colorSettingsSupplier = colorSettingsSupplier;
        this.handler = handler;
    }

    public VisionProcessingConfig(Source<CvImage> imageSource, Pipeline<CvImage> outputPipeline, ScheduledExecutorService executorService, Supplier<HsvColorSettings> colorSettingsSupplier) {
        this(imageSource, outputPipeline, executorService, colorSettingsSupplier, Throwables.silentHandler());
    }

    public VisionProcessingConfig(Source<CvImage> imageSource, Pipeline<CvImage> outputPipeline, ScheduledExecutorService executorService, HsvColorSettings colorSettingsSupplier) {
        this(imageSource, outputPipeline, executorService, () -> colorSettingsSupplier, Throwables.silentHandler());
    }

    public Source<CvImage> getImageSource() {
        return imageSource;
    }

    public Pipeline<CvImage> getOutputPipeline() {
        return outputPipeline;
    }

    public ScheduledExecutorService getExecutorService() {
        return executorService;
    }

    public HsvColorSettings getColorSettings() {
        return colorSettingsSupplier.get();
    }

    public ThrowableHandler getHandler() {
        return handler;
    }
}
