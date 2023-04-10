package com.flash3388.flashlib.vision.control.message;

import com.castle.io.TypedSerializer;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class OptionChangeMessage implements Message {

    public static final MessageType TYPE = MessageType.create(1144, OptionChangeMessage::readFrom);

    private final String mName;
    private final Object mValue;

    public OptionChangeMessage(String name, Object value) {
        mName = name;
        mValue = value;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public String getName() {
        return mName;
    }

    public Object getValue() {
        return mValue;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeUTF(mName);

        TypedSerializer serializer = new TypedSerializer();
        serializer.writeTyped(output, mValue);
    }

    private static OptionChangeMessage readFrom(DataInput dataInput) throws IOException {
        String name = dataInput.readUTF();

        TypedSerializer serializer = new TypedSerializer();
        Object value = serializer.readTyped(dataInput);

        return new OptionChangeMessage(name, value);
    }
}
