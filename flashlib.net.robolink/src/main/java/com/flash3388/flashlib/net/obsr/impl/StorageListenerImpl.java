package com.flash3388.flashlib.net.obsr.impl;

import com.beans.Property;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.impl.messages.EntryChangeMessage;

public class StorageListenerImpl implements StorageListener {

    private final Property<Messenger> mMessenger;

    public StorageListenerImpl(Property<Messenger> messenger) {
        mMessenger = messenger;
    }

    @Override
    public void onEntryUpdate(StoragePath path, Value value) {
        Message message = new EntryChangeMessage(path.toString(), value, 0);
        send(message);
    }

    @Override
    public void onEntryClear(StoragePath path) {
        Message message = new EntryChangeMessage(path.toString(), Value.empty(), EntryChangeMessage.FLAG_CLEARED);
        send(message);
    }

    @Override
    public void onEntryDeleted(StoragePath path) {
        Message message = new EntryChangeMessage(path.toString(), Value.empty(), EntryChangeMessage.FLAG_DELETED);
        send(message);
    }

    private void send(Message message) {
        Messenger messenger = mMessenger.get();
        if (messenger == null) {
            return;
        }

        messenger.send(message);
    }
}
