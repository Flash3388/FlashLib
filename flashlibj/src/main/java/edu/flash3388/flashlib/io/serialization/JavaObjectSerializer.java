package edu.flash3388.flashlib.io.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaObjectSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T value) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        try {
            objectOutputStream.writeObject(value);
        } finally {
            objectOutputStream.close();
            outputStream.close();
        }

        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] serializedValue, Class<T> type) throws IOException, TypeException {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedValue);
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            Object deserializedObject = objectInputStream.readObject();
            return type.cast(deserializedObject);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new TypeException(e);
        } finally {
            objectInputStream.close();
            inputStream.close();
        }
    }
}
