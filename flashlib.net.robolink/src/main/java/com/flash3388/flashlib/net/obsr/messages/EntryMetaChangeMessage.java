package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class EntryMetaChangeMessage implements Message {

    public enum ChangeType {
        CLEAR,
        DELETE
    }

    public static final MessageType TYPE = MessageType.create(100002,
            EntryMetaChangeMessage::readFrom,
            EntryMetaChangeMessage::writeTo);

    private final ChangeType mChangeType;
    private final String mEntryPath;

    public EntryMetaChangeMessage(ChangeType changeType, String entryPath) {
        mChangeType = changeType;
        mEntryPath = entryPath;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public ChangeType getChangeType() {
        return mChangeType;
    }

    public String getEntryPath() {
        return mEntryPath;
    }

    private static EntryMetaChangeMessage readFrom(DataInput input) throws IOException {
        ChangeType changeType = ChangeType.values()[input.readInt()];
        String entryPath = input.readUTF();
        return new EntryMetaChangeMessage(changeType, entryPath);
    }

    private static void writeTo(Message message, DataOutput output) throws IOException {
        EntryMetaChangeMessage actualMessage = (EntryMetaChangeMessage) message;
        output.writeInt(actualMessage.mChangeType.ordinal());
        output.writeUTF(actualMessage.mEntryPath);
    }
}
