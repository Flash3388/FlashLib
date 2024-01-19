package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.messaging.MessageListener;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.net.obsr.Storage;

public class StorageMessageListener implements MessageListener {

    private final Storage mStorage;

    public StorageMessageListener(Storage storage) {
        mStorage = storage;
    }

    @Override
    public void onNewMessage(NewMessageEvent event) {
        mStorage.updateFromMessage(event);
    }
}
