package com.flash3388.flashlib.io.serialization;

import com.flash3388.flashlib.io.StreamReader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

public class JavaObjectSerializer implements Serializer {

    @Override
    public <T> byte[] serialize(T value) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try(ObjectOutput objectOutputStream = new ObjectOutputStream(outputStream)) {
            objectOutputStream.writeObject(value);
        }

        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] serializedValue, Class<T> type) throws IOException, TypeException {
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(serializedValue);
            ObjectInput objectInputStream = new ObjectInputStream(inputStream)) {
            Object deserializedObject = objectInputStream.readObject();
            return type.cast(deserializedObject);
        } catch (ClassNotFoundException | ClassCastException e) {
            throw new TypeException(e);
        }
    }

    @Override
    public <T> void serialize(T value, OutputStream outputStream) throws IOException {
        byte[] data = serialize(value);
        outputStream.write(data);
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, TypeException {
        StreamReader reader = new StreamReader(inputStream); // DO NOT CLOSE THIS, API STATES NO CLOSING
        return deserialize(reader.readAll(), type);
    }
}
