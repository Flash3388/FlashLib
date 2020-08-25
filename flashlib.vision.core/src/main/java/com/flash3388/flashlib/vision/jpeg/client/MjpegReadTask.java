package com.flash3388.flashlib.vision.jpeg.client;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.jpeg.reader.JpegReader;
import org.slf4j.Logger;

import java.io.IOException;

public class MjpegReadTask implements Runnable {

    private final JpegReader mJpegReader;
    private final Pipeline<JpegImage> mPipeline;
    private final Logger mLogger;

    public MjpegReadTask(JpegReader jpegReader, Pipeline<JpegImage> pipeline, Logger logger) {
        mJpegReader = jpegReader;
        mPipeline = pipeline;
        mLogger = logger;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                JpegImage image = mJpegReader.read();
                mPipeline.process(image);
            } catch (IOException | VisionException e) {
                mLogger.error("Error reading and handling image", e);
            }
        }
    }
}
