package com.flash3388.flashlib.vision.control;

import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.vision.control.message.NewAnalysisMessage;
import com.flash3388.flashlib.vision.control.message.OptionChangeMessage;
import com.flash3388.flashlib.vision.control.message.RunStatusMessage;
import com.flash3388.flashlib.vision.control.message.StartMessage;
import com.flash3388.flashlib.vision.control.message.StopMessage;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Helper {
    private Helper() {}

    public static Set<MessageType> getMessageTypes() {
        return new HashSet<>(Arrays.asList(
                NewAnalysisMessage.TYPE,
                OptionChangeMessage.TYPE,
                RunStatusMessage.TYPE,
                StartMessage.TYPE,
                StopMessage.TYPE
        ));
    }

    public static KnownVisionOptions getOptions() {
        KnownVisionOptions options = new KnownVisionOptions();
        StandardVisionOptions.fill(options);

        return options;
    }
}
