package com.flash3388.flashlib.vision;

/**
 * Represents a camera, which is a source of images.
 *
 * @param <T> type of image
 *
 * @since FlashLib 3.0.0
 */
public interface Camera<T extends Image> extends Source<T>, AutoCloseable {

    /**
     * Gets FPS of the camera.
     *
     * @return frames per second.
     */
    int getFps();

    /**
     * Gets height of the camera. Any image produced by the camera will fit this height.
     *
     * @return height in pixels.
     */
    int getHeight();

    /**
     * Gets the width of the images. Any image produced by the camera will fit this width.
     *
     * @return width in pixels.
     */
    int getWidth();

    /**
     * Captures an image from the camera.
     *
     * @return an image from the camera.
     * @throws VisionException if an error occurs while retrieving an image.
     */
    T capture() throws VisionException;

    /**
     * {@inheritDoc}
     * <p>
     *     Invokes {@link #capture()}.
     * </p>
     */
    @Override
    default T get() throws VisionException {
        return capture();
    }
}
