package com.flash3388.flashlib.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serializer {

    /**
     * Serialize a given value into an {@link OutputStream}.
     * <p>
     *     <b>Note:</b> The {@link OutputStream} is not closed by this method.
     * </p>
     *
     * @param value value to serialize.
     * @param outputStream output stream to serialize into.
     * @param <T> type of the value to serialize.
     *
     * @throws IOException if an I/O error has occurred.
     */
    <T> void serialize(T value, OutputStream outputStream) throws IOException;

    /**
     * Deserialize a value of a given type from an {@link InputStream}.
     * <p>
     *      <b>Note:</b> The {@link InputStream} is not closed by this method.
     * </p>
     * @param inputStream output stream to serialize into.
     * @param type type of the value expected to be read
     * @param <T> type of the value to deserialize.
     * @return returns the value deserialized from the stream.
     *
     * @throws IOException if an I/O error has occurred.
     * @throws TypeException if the type of the value deserialized does not matched the wanted <code>type</code>.
     */
    <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, TypeException;

    /**
     * Serialized a given value into binary data (<code>byte[]</code>).
     *
     * @param value value to serialize.
     * @param <T> type of the value to serialize.
     * @return the serialized data representing the given value.
     *
     * @throws IOException if an I/O error has occurred.
     *
     * @see #serialize(Object, OutputStream)
     */
    default <T> byte[] serialize(T value) throws IOException {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            serialize(value, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * Deserialized the given binary data into a value of the wanted type.
     *
     * @param serializedValue data to deserialize.
     * @param type type to deserialize into.
     * @param <T> type of the data to deserialize into.
     * @return the deserialized value.
     *
     * @throws IOException if an I/O error has occurred.
     * @throws TypeException if the deserialized value is not of the wanted type <code>type</code>.
     *
     * @see #deserialize(InputStream, Class)
     */
    default <T> T deserialize(byte[] serializedValue, Class<T> type) throws IOException, TypeException {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedValue)) {
            return deserialize(byteArrayInputStream, type);
        }
    }
}
