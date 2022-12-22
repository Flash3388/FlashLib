package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.obsr.Value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryChangeMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100001, EntryChangeMessage::new);

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

    @Override
    public void readFrom(DataInput input) throws IOException {
        mEntryPath = input.readUTF();
        mValue = EntryHelper.readValueFrom(input);
    }
}
