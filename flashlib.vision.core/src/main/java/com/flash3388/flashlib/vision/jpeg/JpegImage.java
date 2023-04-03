package com.flash3388.flashlib.vision.jpeg;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.color.ColorSpace;

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
        return new JpegImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB));
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
    public boolean isEmpty() {
        return mImage.getData().getDataBuffer().getSize() == 0;
    }

    @Override
    public byte[] getRaw() throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(mImage, JPEG_FORMAT_NAME, byteArrayOutputStream);

            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public java.awt.Image toAwt() {
        return mImage;
    }

    @Override
    public ColorSpace getColorSpace() {
        return ColorSpace.BGR;
    }
}
