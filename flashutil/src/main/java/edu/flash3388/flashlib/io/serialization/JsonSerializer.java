package edu.flash3388.flashlib.io.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.charset.Charset;

public class JsonSerializer implements Serializer {

    private final Gson mGson;
    private final Charset mCharset;

    public JsonSerializer(Gson gson, Charset charset) {
        mGson = gson;
        mCharset = charset;
    }

    @Override
    public <T> byte[] serialize(T value) throws IOException {
        try {
            String json = mGson.toJson(value);
            return json.getBytes(mCharset);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] serializedValue, Class<T> type) throws IOException, TypeException {
        try {
            Object value = mGson.fromJson(new String(serializedValue, mCharset), type);
            return type.cast(value);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new IOException(e);
        } catch (ClassCastException e) {
            throw new TypeException(e);
        }
    }
}
