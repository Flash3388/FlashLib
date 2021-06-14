package com.flash3388.flashlib.vision.analysis;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class AnalysisSerializerTest {

    @Test
    public void serializeAndDeserialize_forFakeData_producesSameClass() throws Exception {
        Map<String, Object> targetProps = new HashMap<>();
        targetProps.put("hey", "hello");
        targetProps.put("bakum", 464);
        targetProps.put("hey3", false);
        List<FakeTarget> targets = Arrays.asList(
                new FakeTarget(targetProps)
        );

        Map<String, Object> props = new HashMap<>();
        props.put("hasdsdey", "hks");
        props.put("number", 23214.54677777);
        props.put("another", true);

        FakeAnalysis analysis = new FakeAnalysis(targets, props);

        AnalysisSerializer serializer = new AnalysisSerializer();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try(DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            serializer.serializeTo(dataOutputStream, analysis);
            dataOutputStream.flush();
        }

        Analysis parsedAnalysis;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            parsedAnalysis = serializer.deserializeFrom(dataInputStream);
        }

        assertThat(parsedAnalysis, equalTo(analysis));
    }

    @Test
    public void serializeAndDeserialize_forJsonData_producesSameClass() throws Exception {
        JsonAnalysis analysis = new JsonAnalysis.Builder()
                .put("hello", 54)
                .put("Bam", false)
                .put("str", "hellostr")
                .buildTarget()
                    .put("something", 216.54)
                    .put("and other", "yep")
                    .build()
                .build();

        AnalysisSerializer serializer = new AnalysisSerializer();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try(DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            serializer.serializeTo(dataOutputStream, analysis);
            dataOutputStream.flush();
        }

        Analysis parsedAnalysis;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try (DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            parsedAnalysis = serializer.deserializeFrom(dataInputStream);
        }

        assertThat(parsedAnalysis, equalTo(analysis));
    }
}