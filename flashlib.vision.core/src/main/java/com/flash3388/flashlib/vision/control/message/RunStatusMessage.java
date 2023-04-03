package com.flash3388.flashlib.vision.control.message;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class RunStatusMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1133, RunStatusMessage::readFrom);

    private final boolean mStatus;

    public RunStatusMessage(boolean status) {
        mStatus = status;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public boolean isRunning() {
        return mStatus;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeBoolean(mStatus);
    }

    private static RunStatusMessage readFrom(DataInput dataInput) throws IOException {
        return new RunStatusMessage(
                dataInput.readBoolean()
        );
    }
}
