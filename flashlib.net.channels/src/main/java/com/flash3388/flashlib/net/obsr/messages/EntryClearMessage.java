package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryClearMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100002, EntryClearMessage::new);

    private String mEntryPath;

    public EntryClearMessage(String entryPath) {
        mEntryPath = entryPath;
    }

    private EntryClearMessage() {
    }

    @Override
    public MessageType getType() {
        return TYPE;
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
