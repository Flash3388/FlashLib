package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.obsr.BasicEntry;
import com.flash3388.flashlib.net.obsr.EntryValueType;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageContentsMessage implements Message {

    public static final MessageType TYPE = MessageType.createType(100004, StorageContentsMessage::new);

    private Map<String, BasicEntry> mEntries;

    public StorageContentsMessage(Map<String, BasicEntry> entries) {
        mEntries = entries;
    }

    private StorageContentsMessage() {
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public Map<String, BasicEntry> getEntries() {
        return mEntries;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mEntries.size());
        for (Map.Entry<String, BasicEntry> entry : mEntries.entrySet()) {
            output.writeUTF(entry.getKey());
            EntryHelper.writeTypeTo(output, entry.getValue().getType());
            EntryHelper.writeValueTo(output, entry.getValue().getType(), entry.getValue().getValue());
        }
    }

    @Override
    public void readFrom(DataInput input) throws IOException {
        int size = input.readInt();

        Map<String, BasicEntry> entries = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String path = input.readUTF();
            EntryValueType type = EntryHelper.readTypeFrom(input);
            Object value = EntryHelper.readValueFrom(input, type);

            entries.put(path, new BasicEntry(type, value));
        }

        mEntries = entries;
    }
}
