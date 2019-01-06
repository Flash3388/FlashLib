package edu.flash3388.flashlib.cam.jpeg.client;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;
import edu.flash3388.flashlib.cam.jpeg.reader.JpegReader;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MjpegReadTask implements Runnable {

    private final JpegReader mJpegReader;
    private final Consumer<JpegImage> mImageConsumer;
    private final Logger mLogger;

    public MjpegReadTask(JpegReader jpegReader, Consumer<JpegImage> imageConsumer, Logger logger) {
        mJpegReader = jpegReader;
        mImageConsumer = imageConsumer;
        mLogger = logger;
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                JpegImage image = mJpegReader.read();
                mImageConsumer.accept(image);
            } catch (IOException e) {
                mLogger.log(Level.SEVERE, "error reading jpeg", e);
            }
        }
    }
}
