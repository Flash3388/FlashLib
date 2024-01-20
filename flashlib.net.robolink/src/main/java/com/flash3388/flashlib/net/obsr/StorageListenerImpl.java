package com.flash3388.flashlib.net.obsr;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.Messenger;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;

class StorageListenerImpl implements StorageListener {

    private final Messenger mMessenger;

    public StorageListenerImpl(Messenger messenger) {
        mMessenger = messenger;
    }

    @Override
    public void onEntryUpdate(StoragePath path, Value value) {
        Message message = new EntryChangeMessage(path.toString(), value, 0);
        mMessenger.send(message);
    }

    @Override
    public void onEntryClear(StoragePath path) {
        Message message = new EntryChangeMessage(path.toString(), Value.empty(), EntryChangeMessage.FLAG_CLEARED);
        mMessenger.send(message);
    }

    @Override
    public void onEntryDeleted(StoragePath path) {
        Message message = new EntryChangeMessage(path.toString(), Value.empty(), EntryChangeMessage.FLAG_DELETED);
        mMessenger.send(message);
    }
}
