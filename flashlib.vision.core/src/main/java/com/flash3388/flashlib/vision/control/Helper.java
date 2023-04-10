package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.vision.control.message.NewAnalysisMessage;
import com.flash3388.flashlib.vision.control.message.OptionChangeMessage;
import com.flash3388.flashlib.vision.control.message.RunStatusMessage;
import com.flash3388.flashlib.vision.control.message.StartMessage;
import com.flash3388.flashlib.vision.control.message.StopMessage;

public class Helper {
    private Helper() {}

    public static KnownMessageTypes getMessageTypes() {
        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(NewAnalysisMessage.TYPE);
        messageTypes.put(OptionChangeMessage.TYPE);
        messageTypes.put(RunStatusMessage.TYPE);
        messageTypes.put(StartMessage.TYPE);
        messageTypes.put(StopMessage.TYPE);

        return messageTypes;
    }

    public static KnownVisionOptions getOptions() {
        KnownVisionOptions options = new KnownVisionOptions();
        StandardVisionOptions.fill(options);

        return options;
    }
}
