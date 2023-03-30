package com.flash3388.flashlib.net.obsr.messages;


import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.OutMessage;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NewEntryMessage implements InMessage, OutMessage {

    public static final MessageType TYPE = MessageType.create(100003, NewEntryMessage::readFrom);

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

    private static NewEntryMessage readFrom(DataInput input) throws IOException {
        String entryPath = input.readUTF();
        return new NewEntryMessage(entryPath);
    }
}
