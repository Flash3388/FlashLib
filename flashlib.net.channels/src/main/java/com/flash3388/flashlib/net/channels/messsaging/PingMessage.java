package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Time;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PingMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1,
            PingMessage::readFrom,
            PingMessage::writeInto);

    private final ChannelId mOriginalSender;
    private final Time mTime;

    public PingMessage(ChannelId originalSender, Time time) {
        mOriginalSender = originalSender;
        mTime = time;
    }

    public ChannelId getOriginalSender() {
        return mOriginalSender;
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
        actualMessage.mOriginalSender.writeInto(output);
        output.writeLong(actualMessage.mTime.valueAsMillis());
    }

    private static PingMessage readFrom(DataInput input) throws IOException {
        ChannelId originalSender = new ChannelId(input);
        long timeMillis = input.readLong();
        Time time = Time.milliseconds(timeMillis);
        return new PingMessage(originalSender, time);
    }
}
