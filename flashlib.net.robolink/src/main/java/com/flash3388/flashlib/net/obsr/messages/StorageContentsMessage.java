package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.channels.messsaging.Message;
import com.flash3388.flashlib.net.channels.messsaging.MessageType;
import com.flash3388.flashlib.net.channels.messsaging.OutMessage;
import com.flash3388.flashlib.net.obsr.Value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageContentsMessage implements Message, OutMessage {

    public static final MessageType TYPE = MessageType.create(100004, StorageContentsMessage::readFrom);

    private Map<String, Value> mEntries;

    public StorageContentsMessage(Map<String, Value> entries) {
        mEntries = entries;
    }

    private StorageContentsMessage() {
    }

    public Map<String, Value> getEntries() {
        return mEntries;
    }

    @Override
    public void writeInto(DataOutput output) throws IOException {
        output.writeInt(mEntries.size());
        for (Map.Entry<String, Value> entry : mEntries.entrySet()) {
            output.writeUTF(entry.getKey());
            EntryHelper.writeValueTo(output, entry.getValue());
        }
    }

    private static StorageContentsMessage readFrom(DataInput input) throws IOException {
        int size = input.readInt();

        Map<String, Value> entries = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            String path = input.readUTF();
            Value value = EntryHelper.readValueFrom(input);

            entries.put(path, value);
        }

        return new StorageContentsMessage(entries);
    }
}
