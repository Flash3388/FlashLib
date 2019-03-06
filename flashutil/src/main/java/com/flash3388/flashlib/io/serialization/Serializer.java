package com.flash3388.flashlib.io.serialization;

import java.io.IOException;

public interface Serializer {

    <T> byte[] serialize(T value) throws IOException;
    <T> T deserialize(byte[] serializedValue, Class<T> type) throws IOException, TypeException;
}
