package edu.flash3388.flashlib.io.serialization;

import java.io.*;

public interface Serializer {

    <T> void serialize(OutputStream outputStream, T value) throws IOException;

    <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, ClassNotFoundException;
}
