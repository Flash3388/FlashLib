package com.flash3388.flashlib.vision.messages;

import com.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import com.flash3388.flashlib.io.serialization.Serializer;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Test;

public class AnalysisMessageTest {

    @Test
    public void serialization_normal_analysisStatePassedCorrectly() throws Exception {
        final String DATA = "{\"data\":\"test\"}";

        Serializer serializer = new JavaObjectSerializer();
        Analysis analysis = new Analysis(new JsonParser().parse(DATA).getAsJsonObject());
        AnalysisMessage analysisMessage = new AnalysisMessage(analysis);

        byte[] serialized = serializer.serialize(analysisMessage);
        AnalysisMessage deserialized = serializer.deserialize(serialized, AnalysisMessage.class);

        Assert.assertEquals(DATA, deserialized.getAnalysis().getData().toString());
    }
}