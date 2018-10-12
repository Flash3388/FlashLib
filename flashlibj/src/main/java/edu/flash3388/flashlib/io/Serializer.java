package edu.flash3388.flashlib.io;

import java.io.*;

public class Serializer {

    public <T> void serialize(OutputStream outputStream, T value) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(value);
    }

    public <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        Object deserializedObject = objectInputStream.readObject();
        return type.cast(deserializedObject);
    }
}
