package com.flash3388.flashlib.vision.analysis;

import java.util.NoSuchElementException;

/**
 * Represents a target detected during a vision process.
 *
 * @since FlashLib 3.0.0
 */
public interface Target {

    /**
     * Gets whether the target has a value for a named property.
     *
     * @param name name of the property.
     * @return <b>true</b> if has a value, <b>false</b> otherwise.
     *
     * @see #getProperty(String, Class)
     */
    boolean hasProperty(String name);

    /**
     * Gets the value associated with a named property.
     *
     * @param name name of the property.
     * @param type expected type of the value.
     * @param <T> data type of the value.
     *
     * @return value of the property.
     *
     * @throws NoSuchElementException if no value is associated with the name.
     *
     * @see #hasProperty(String)
     */
    <T> T getProperty(String name, Class<T> type);
}
