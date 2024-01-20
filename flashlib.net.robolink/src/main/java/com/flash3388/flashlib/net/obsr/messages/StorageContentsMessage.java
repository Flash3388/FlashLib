package com.flash3388.flashlib.net.obsr.messages;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.obsr.Value;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StorageContentsMessage implements Message {

    // TODO: DEPENDING ON AMOUNT OF DATA, THIS MESSAGE MIGHT BE TOO BIG
    //      CAN PROBABLY BE BROKEN DOWN INTO PIECES

    public static final MessageType TYPE = MessageType.create(100004,
            StorageContentsMessage::readFrom,
            StorageContentsMessage::writeInto);

    private final Map<String, Value> mEntries;

    public StorageContentsMessage(Map<String, Value> entries) {
        mEntries = entries;
    }

    @Override
    public MessageType getType() {
        return TYPE;
    }

    public Map<String, Value> getEntries() {
        return mEntries;
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

    private static void writeInto(Message message, DataOutput output) throws IOException {
        StorageContentsMessage actualMessage = (StorageContentsMessage) message;

        output.writeInt(actualMessage.mEntries.size());
        for (Map.Entry<String, Value> entry : actualMessage.mEntries.entrySet()) {
            output.writeUTF(entry.getKey());
            EntryHelper.writeValueTo(output, entry.getValue());
        }
    }
}
