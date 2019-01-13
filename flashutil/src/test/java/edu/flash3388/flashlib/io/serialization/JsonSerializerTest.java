package edu.flash3388.flashlib.io.serialization;

import com.google.gson.Gson;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

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