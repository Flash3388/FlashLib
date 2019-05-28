package com.flash3388.flashlib.io.serialization;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class JavaObjectSerializerTest {

    private static final double DOUBLE_EQUALS_MARGIN = 0.0001;

    @Test
    public void serialization_objectStateRemains() throws Exception {
        final TestClass OBJECT = new TestClass(23, 5.0);

        JavaObjectSerializer serializer = new JavaObjectSerializer();

        byte[] serializedValue = serializer.serialize(OBJECT);

        TestClass deserializedObject = serializer.deserialize(serializedValue, TestClass.class);
        assertTestClassEquals(OBJECT, deserializedObject);
    }

    @Test(expected = TypeException.class)
    public void deserialize_classTypeDoesNotMatch_throwsTypeException() throws Exception {
        final TestClass OBJECT = new TestClass(23, 5.0);

        JavaObjectSerializer serializer = new JavaObjectSerializer();

        byte[] serializedValue = serializer.serialize(OBJECT);
        serializer.deserialize(serializedValue, OtherObject.class);
    }

    @Test(expected = IOException.class)
    public void serialize_objectNotSerializable_throwsIOException() throws Exception {
        JavaObjectSerializer serializer = new JavaObjectSerializer();
        serializer.serialize(new OtherObject());
    }

    @Test
    public void serialize_intoOutputStream_streamIsNotClosed() throws Exception {
        JavaObjectSerializer serializer = new JavaObjectSerializer();

        OutputStream outputStream = mock(OutputStream.class);
        serializer.serialize(new TestClass(0, 0.0), outputStream);

        verify(outputStream, times(0)).close();
    }

    @Test
    public void deserialize_fromInputStream_streamIsNotClosed() throws Exception {
        JavaObjectSerializer serializer = new JavaObjectSerializer();

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

    private static class TestClass implements Serializable {

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