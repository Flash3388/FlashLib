package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.OutMessage;
import com.flash3388.flashlib.net.obsr.Value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryChangeMessage implements InMessage, OutMessage {

    public static final MessageType TYPE = MessageType.create(100001, EntryChangeMessage::readFrom);

    private String mEntryPath;
    private Value mValue;

    public EntryChangeMessage(String entryPath, Value value) {
        mEntryPath = entryPath;
        mValue = value;
    }

    private EntryChangeMessage() {

    }

    public String getEntryPath() {
        return mEntryPath;
    }

    public Value getValue() {
        return mValue;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeUTF(mEntryPath);
        EntryHelper.writeValueTo(output, mValue);
    }

    private static EntryChangeMessage readFrom(DataInput input) throws IOException {
        String entryPath = input.readUTF();
        Value value = EntryHelper.readValueFrom(input);
        return new EntryChangeMessage(entryPath, value);
    }
}
