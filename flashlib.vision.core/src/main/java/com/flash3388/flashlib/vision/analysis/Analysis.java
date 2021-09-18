package com.flash3388.flashlib.vision.analysis;

import java.io.DataOutput;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An analysis produced from a vision process.
 *
 * @since FlashLib 2.0.0
 */
public interface Analysis {

    static JsonAnalysis.Builder builder() {
        return new JsonAnalysis.Builder();
    }

    /**
     * Gets all targets detected.
     *
     * @return list of {@link Target targets}.
     */
    List<? extends Target> getDetectedTargets();

    /**
     * Gets whether the analysis has a value for a named property.
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

    /**
     * Serializes the analysis data into an output source.
     *
     * @param dataOutput source to serialize data into.
     *
     * @throws IOException if an I/O error has occurred.
     */
    void serializeTo(DataOutput dataOutput) throws IOException;
}
