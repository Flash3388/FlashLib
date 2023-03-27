package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryDeleteMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100005, EntryDeleteMessage::new);

    private String mEntryPath;

    public EntryDeleteMessage(String entryPath) {
        mEntryPath = entryPath;
    }

    private EntryDeleteMessage() {
    }

    public String getEntryPath() {
        return mEntryPath;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeUTF(mEntryPath);
    }

    @Override
    public void readFrom(DataInput input) throws IOException {
        mEntryPath = input.readUTF();
    }
}
