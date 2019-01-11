package edu.flash3388.flashlib.vision.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import edu.flash3388.flashlib.communication.message.Message;
import edu.flash3388.flashlib.vision.processing.analysis.Analysis;

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
        try {
            String rawData = objectInputStream.readUTF();
            JsonElement jsonElement = new JsonParser().parse(rawData);
            if (!jsonElement.isJsonObject()) {
                throw new IOException("json data is not a json object");
            }

            mAnalysis = new Analysis(jsonElement.getAsJsonObject());
        } catch (JsonParseException e) {
            throw new IOException(e);
        }
    }
}
