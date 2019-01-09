package edu.flash3388.flashlib.vision.jpeg.client;

import edu.flash3388.flashlib.vision.ImagePipeline;
import edu.flash3388.flashlib.vision.jpeg.JpegImage;
import edu.flash3388.flashlib.vision.jpeg.reader.JpegReader;
import edu.flash3388.flashlib.vision.processing.exceptions.ImageProcessingException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                mLogger.log(Level.SEVERE, "error handling jpeg", e);
            }
        }
    }
}
