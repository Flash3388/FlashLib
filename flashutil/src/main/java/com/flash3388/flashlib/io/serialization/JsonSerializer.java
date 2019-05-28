package com.flash3388.flashlib.io.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class JsonSerializer implements Serializer {

    private final Gson mGson;
    private final Charset mCharset;

    public JsonSerializer(Gson gson, Charset charset) {
        mGson = gson;
        mCharset = charset;
    }

    @Override
    public <T> void serialize(T value, OutputStream outputStream) throws IOException {
        try {
            String json = mGson.toJson(value);
            byte[] bytes = json.getBytes(mCharset);
            outputStream.write(bytes);
        } catch (JsonIOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> type) throws IOException, TypeException {
        try {
            InputStreamReader reader = new InputStreamReader(inputStream, mCharset);
            Object value = mGson.fromJson(reader, type);
            return type.cast(value);
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new IOException(e);
        } catch (ClassCastException e) {
            throw new TypeException(e);
        }
    }
}
