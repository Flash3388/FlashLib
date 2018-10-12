package edu.flash3388.flashlib.io;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void serialize_deserialize_objectStateRemains() throws Exception {
        TestClass OBJECT = new TestClass(23, 5.0);

        Serializer serializer = new Serializer();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            serializer.serialize(outputStream, OBJECT);
        } finally {
            outputStream.close();
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            TestClass deserializedObject = serializer.deserialize(inputStream, TestClass.class);

            assertTrue(OBJECT.equals(deserializedObject));
        } finally {
            inputStream.close();
        }
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
}