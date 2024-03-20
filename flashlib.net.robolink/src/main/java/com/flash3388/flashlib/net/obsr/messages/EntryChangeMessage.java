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

    // bits 1 & 2, can't be both
    public static final int FLAG_CLEARED = 1;
    public static final int FLAG_DELETED = 2;

    private final String mEntryPath;
    private final Value mValue;
    private final int mFlags;

    public EntryChangeMessage(String entryPath, Value value, int flags) {
        mEntryPath = entryPath;
        mValue = value;
        mFlags = flags;
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

    public int getFlags() {
        return mFlags;
    }

    private static void writeInto(Message message, DataOutput output) throws IOException {
        EntryChangeMessage actualMessage = (EntryChangeMessage) message;
        output.writeUTF(actualMessage.mEntryPath);
        EntryHelper.writeValueTo(output, actualMessage.mValue);
        output.writeInt(actualMessage.mFlags);
    }

    private static EntryChangeMessage readFrom(DataInput input) throws IOException {
        String entryPath = input.readUTF();
        Value value = EntryHelper.readValueFrom(input);
        int flags = input.readInt();
        return new EntryChangeMessage(entryPath, value, flags);
    }
}
