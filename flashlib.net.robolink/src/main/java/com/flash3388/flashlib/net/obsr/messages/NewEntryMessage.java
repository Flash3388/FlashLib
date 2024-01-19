package com.flash3388.flashlib.net.obsr.messages;


import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NewEntryMessage implements Message {

    public static final MessageType TYPE = MessageType.create(100003,
            NewEntryMessage::readFrom,
            NewEntryMessage::writeTo);

    private String mEntryPath;

    public NewEntryMessage(String entryPath) {
        mEntryPath = entryPath;
    }

    private NewEntryMessage() {
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public String getEntryPath() {
        return mEntryPath;
    }

    private static NewEntryMessage readFrom(DataInput input) throws IOException {
        String entryPath = input.readUTF();
        return new NewEntryMessage(entryPath);
    }

    private static void writeTo(Message message, DataOutput output) throws IOException {
        NewEntryMessage actualMessage = (NewEntryMessage) message;
        output.writeUTF(actualMessage.mEntryPath);
    }
}
