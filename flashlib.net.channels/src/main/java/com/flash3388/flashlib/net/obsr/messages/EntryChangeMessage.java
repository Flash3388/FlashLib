package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.obsr.EntryType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryChangeMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100001, EntryChangeMessage::new);

    private String mEntryPath;
    private EntryType mType;
    private Object mValue;

    public EntryChangeMessage(String entryPath, EntryType type, Object value) {
        mEntryPath = entryPath;
        mType = type;
        mValue = value;
    }

    private EntryChangeMessage() {

    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public String getEntryPath() {
        return mEntryPath;
    }

    public EntryType getEntryType() {
        return mType;
    }

    public Object getValue() {
        return mValue;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeUTF(mEntryPath);
        EntryHelper.writeTypeTo(output, mType);
        EntryHelper.writeValueTo(output, mType, mValue);
    }

    @Override
    public void readFrom(DataInput input) throws IOException {
        mEntryPath = input.readUTF();
        mType = EntryHelper.readTypeFrom(input);
        mValue = EntryHelper.readValueFrom(input, mType);
    }
}
