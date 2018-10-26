package edu.flash3388.flashlib.io.serialization;

import java.io.*;

public class JavaObjectSerializer implements Serializer {

    @Override
    public <T> void serialize(OutputStream outputStream, T value) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(value);
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object deserializedObject = objectInputStream.readObject();
        return type.cast(deserializedObject);
    }
}
