package com.flash3388.flashlib.vision.jpeg.client;

import com.flash3388.flashlib.vision.ImagePipeline;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.jpeg.reader.JpegReader;
import com.flash3388.flashlib.vision.processing.ImageProcessingException;
import org.slf4j.Logger;

import java.io.IOException;

public class MjpegReadTask implements Runnable {

    private final JpegReader mJpegReader;
    private final ImagePipeline<JpegImage> mImagePipeline;
    private final Logger mLogger;

    public MjpegReadTask(JpegReader jpegReader, ImagePipeline<JpegImage> imagePipeline, Logger logger) {
        mJpegReader = jpegReader;
        mImagePipeline = imagePipeline;
        mLogger = logger;
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                JpegImage image = mJpegReader.read();
                mImagePipeline.process(image);
            } catch (IOException | ImageProcessingException e) {
                mLogger.error("Error reading and handling image", e);
            }
        }
    }
}
