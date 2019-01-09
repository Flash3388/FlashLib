package edu.flash3388.flashlib.vision.messages;

import edu.flash3388.flashlib.io.serialization.JavaObjectSerializer;
import edu.flash3388.flashlib.io.serialization.Serializer;
import edu.flash3388.flashlib.vision.processing.analysis.Analysis;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class AnalysisMessageTest {

    @Test
    public void serialization_normal_analysisStatePassedCorrectly() throws Exception {
        final String DATA = "{\"data\":\"test\"}";

        Serializer serializer = new JavaObjectSerializer();
        AnalysisMessage analysisMessage = new AnalysisMessage(new Analysis(new JSONObject(DATA)));

        byte[] serialized = serializer.serialize(analysisMessage);
        AnalysisMessage deserialized = serializer.deserialize(serialized, AnalysisMessage.class);

        assertEquals(DATA, deserialized.getAnalysis().getData().toString());
    }
}