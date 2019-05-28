package com.flash3388.flashlib.io.serialization;

import com.google.gson.Gson;
import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JsonSerializerTest {

    private static final double DOUBLE_EQUALS_MARGIN = 0.0001;

    @Test
    public void serialization_objectStateRemains() throws Exception {
        final TestClass OBJECT = new TestClass(23, 5.0);

        JsonSerializer jsonSerializer = new JsonSerializer(new Gson(), Charset.forName("UTF-8"));
        byte[] serialized = jsonSerializer.serialize(OBJECT);
        TestClass deserializedObject = jsonSerializer.deserialize(serialized, TestClass.class);

        assertTestClassEquals(OBJECT, deserializedObject);
    }

    @Test
    public void serialize_intoOutputStream_streamIsNotClosed() throws Exception {
        JsonSerializer serializer = new JsonSerializer(new Gson(), Charset.forName("UTF-8"));

        Object value = new Object();
        OutputStream outputStream = mock(OutputStream.class);
        serializer.serialize(value, outputStream);

        verify(outputStream, times(0)).close();
    }

    @Test
    public void deserialize_fromInputStream_streamIsNotClosed() throws Exception {
        JsonSerializer serializer = new JsonSerializer(new Gson(), Charset.forName("UTF-8"));

        InputStream inputStream = mock(InputStream.class);
        when(inputStream.read()).thenReturn(0);
        when(inputStream.available()).thenReturn(0);

        try {
            serializer.deserialize(inputStream, TestClass.class);
        } catch (Throwable t) {
            // ignore
        }

        verify(inputStream, times(0)).close();
    }

    private void assertTestClassEquals(TestClass expected, TestClass actual) {
        assertEquals(expected.variable1, actual.variable1);
        assertEquals(expected.variable2, actual.variable2, DOUBLE_EQUALS_MARGIN);
    }

    private static class TestClass {

        private final int variable1;
        private double variable2;

        TestClass(int variable1, double variable2) {
            this.variable1 = variable1;
            this.variable2 = variable2;
        }

        public boolean equals(TestClass testClass) {
            return variable1 == testClass.variable1 && variable2 == testClass.variable2;
        }
    }

    private static class OtherObject {

    }
}