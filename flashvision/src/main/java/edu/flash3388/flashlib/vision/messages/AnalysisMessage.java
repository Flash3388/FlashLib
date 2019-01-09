package edu.flash3388.flashlib.vision.messages;

import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.vision.processing.analysis.Analysis;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class AnalysisMessage implements Message {

    private Analysis mAnalysis;

    public AnalysisMessage(Analysis analysis) {
        mAnalysis = analysis;
    }

    public Analysis getAnalysis() {
        return mAnalysis;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeUTF(mAnalysis.getData().toString());
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        String rawData = objectInputStream.readUTF();
        mAnalysis = new Analysis(new JSONObject(rawData));
    }
}
