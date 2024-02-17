package com.flash3388.flashlib.control;

/**
 * Represents an object capable of stopping its current operation.
 *
 * @since FlashLib 2.0.0
 */
public interface Stoppable {

    /**
     * Stops the current operation.
     * <p>
     *     If no operation is running at the moment, nothing will
     *     happen.
     * </p>
     */
    void stop();
}
