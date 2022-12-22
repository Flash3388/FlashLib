package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessageType;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StorageOpFlag;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import org.slf4j.Logger;

import java.util.EnumSet;

public class ChannelUpdateHandler implements MessagingChannel.UpdateHandler {

    protected final Storage mStorage;
    protected final Logger mLogger;

    protected ChannelUpdateHandler(Storage storage, Logger logger) {
        mStorage = storage;
        mLogger = logger;
    }

    @Override
    public void onNewMessage(MessageInfo messageInfo, Message message) {
        MessageType type = messageInfo.getType();

        if (type.equals(EntryChangeMessage.TYPE)) {
            handleEntryChange((EntryChangeMessage) message);
        } else if (type.equals(NewEntryMessage.TYPE)) {
            handleNewEntry((NewEntryMessage) message);
        } else if (type.equals(EntryClearMessage.TYPE)) {
            handleEntryClear((EntryClearMessage) message);
        } else if (type.equals(StorageContentsMessage.TYPE)) {
            handleStorageContents((StorageContentsMessage) message);
        } else {
            mLogger.debug("Received storage message with unknown type: " + type.getKey());
        }
    }

    private void handleNewEntry(NewEntryMessage message) {
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.createEntry(path, EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION));
    }

    private void handleEntryChange(EntryChangeMessage message) {
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.setEntryValue(path, message.getValue(),
                EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION, StorageOpFlag.FORCE_CHANGE));
    }

    private void handleEntryClear(EntryClearMessage message) {
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.clearEntryValue(path, EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION));
    }

    private void handleStorageContents(StorageContentsMessage message) {
        mStorage.setAll(message.getEntries());
    }
}
