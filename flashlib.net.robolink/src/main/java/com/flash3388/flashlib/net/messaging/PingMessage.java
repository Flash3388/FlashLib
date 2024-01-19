package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PingMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1,
            PingMessage::readFrom,
            PingMessage::writeInto);

    private final Time mTime;

    public PingMessage(Time time) {
        mTime = time;
    }

    public Time getTime() {
        return mTime;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    private static void writeInto(Message message, DataOutput output) throws IOException {
        PingMessage actualMessage = (PingMessage) message;
        output.writeLong(actualMessage.mTime.valueAsMillis());
    }

    private static PingMessage readFrom(DataInput input) throws IOException {
        long timeMillis = input.readLong();
        Time time = Time.milliseconds(timeMillis);
        return new PingMessage(time);
    }
}
