package edu.flash3388.flashlib.vision.jpeg;

import edu.flash3388.flashlib.vision.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JpegImage implements Image {

    private static final String JPEG_FORMAT_NAME = "jpg";

    private final BufferedImage mImage;

    public JpegImage(BufferedImage image) {
        mImage = image;
    }

    public static JpegImage rgb(int height, int width) {
        return new JpegImage(new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB));
    }

    public static JpegImage fromBytes(byte[] imageBytes) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        return new JpegImage(image);
    }

    @Override
    public int getHeight() {
        return mImage.getHeight();
    }

    @Override
    public int getWidth() {
        return mImage.getWidth();
    }

    @Override
    public byte[] getRaw() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(mImage, JPEG_FORMAT_NAME, byteArrayOutputStream);

            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            byteArrayOutputStream.close();

        }
    }

    @Override
    public JpegImage toJpeg() {
        return this;
    }
}
