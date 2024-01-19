package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.obsr.Value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryChangeMessage implements Message {

    public static final MessageType TYPE = MessageType.create(100001,
            EntryChangeMessage::readFrom,
            EntryChangeMessage::writeInto);

    private String mEntryPath;
    private Value mValue;

    public EntryChangeMessage(String entryPath, Value value) {
        mEntryPath = entryPath;
        mValue = value;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public String getEntryPath() {
        return mEntryPath;
    }

    public Value getValue() {
        return mValue;
    }

    private static void writeInto(Message message, DataOutput output) throws IOException {
        EntryChangeMessage actualMessage = (EntryChangeMessage) message;
        output.writeUTF(actualMessage.mEntryPath);
        EntryHelper.writeValueTo(output, actualMessage.mValue);
    }

    private static EntryChangeMessage readFrom(DataInput input) throws IOException {
        String entryPath = input.readUTF();
        Value value = EntryHelper.readValueFrom(input);
        return new EntryChangeMessage(entryPath, value);
    }
}
