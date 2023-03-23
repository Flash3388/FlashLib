package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NewEntryMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100003, NewEntryMessage::new);

    private String mEntryPath;

    public NewEntryMessage(String entryPath) {
        mEntryPath = entryPath;
    }

    private NewEntryMessage() {
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
