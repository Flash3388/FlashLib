package com.flash3388.flashlib.vision.control.message;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.analysis.JsonAnalysis;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NewAnalysisMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1155, NewAnalysisMessage::readFrom);

    private final Analysis mAnalysis;

    public NewAnalysisMessage(Analysis analysis) {
        mAnalysis = analysis;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public Analysis getAnalysis() {
        return mAnalysis;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        mAnalysis.serializeTo(output);
    }

    private static NewAnalysisMessage readFrom(DataInput dataInput) throws IOException {
        Analysis analysis = new JsonAnalysis(dataInput);
        return new NewAnalysisMessage(analysis);
    }
}
