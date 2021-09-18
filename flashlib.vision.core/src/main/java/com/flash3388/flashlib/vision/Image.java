package com.flash3388.flashlib.vision;

import java.io.IOException;

/**
 * Represents a generic image object.
 *
 * @since FlashLib 3.0.0
 */
public interface Image {

    /**
     * Gets height of the image.
     *
     * @return height in pixels.
     */
    int getHeight();

    /**
     * Gets the width of the images.
     *
     * @return width in pixels.
     */
    int getWidth();

    /**
     * Gets whether the image is empty. i.e. does not contain any pixels.
     *
     * @return <b>true</b> if empty, <b>false</b> otherwise.
     */
    boolean isEmpty();

    /**
     * Gets the raw data containing the image.
     *
     * @return raw image data.
     * @throws IOException if an error occurs while extracting the raw data.
     */
    byte[] getRaw() throws IOException;

    /**
     * Converts the image to {@link java.awt.Image} object.
     *
     * @return {@link java.awt.Image} object containing the image.
     */
    java.awt.Image toAwt();
}
