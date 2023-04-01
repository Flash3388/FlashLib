package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.channels.messsaging.MessageAndType;
import com.flash3388.flashlib.net.channels.messsaging.MessageInfo;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.messaging.OutMessage;
import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StorageOpFlag;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryDeleteMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.RequestContentMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.function.BiConsumer;

public class ChannelUpdateHandler implements MessagingChannel.UpdateHandler {

    protected final Storage mStorage;
    protected final Logger mLogger;
    private final Queue<PendingWriteMessage> mWriteQueue;

    protected ChannelUpdateHandler(Storage storage,
                                   Logger logger,
                                   Queue<PendingWriteMessage> writeQueue) {
        mStorage = storage;
        mLogger = logger;
        mWriteQueue = writeQueue;
    }

    @Override
    public void onNewMessage(MessageInfo info, InMessage message) {
        MessageType type = info.getType();

        if (type.equals(EntryChangeMessage.TYPE)) {
            handleEntryChange((EntryChangeMessage) message);
        } else if (type.equals(NewEntryMessage.TYPE)) {
            handleNewEntry((NewEntryMessage) message);
        } else if (type.equals(EntryClearMessage.TYPE)) {
            handleEntryClear((EntryClearMessage) message);
        } else if (type.equals(StorageContentsMessage.TYPE)) {
            handleStorageContents((StorageContentsMessage) message);
        } else if (type.equals(EntryDeleteMessage.TYPE)) {
            handleEntryDelete((EntryDeleteMessage) message);
        } else if (type.equals(RequestContentMessage.TYPE)) {
            handleRequestContent((RequestContentMessage) message);
        } else {
            mLogger.debug("Received storage message with unknown type: " + type.getKey());
        }
    }

    @Override
    public Optional<List<MessageAndType>> getMessageForNewClient() {
        mLogger.debug("New client. Sending contents of storage");
        Map<String, Value> all = mStorage.getAll();
        List<MessageAndType> messages = new ArrayList<>(all.size());
        for (Map.Entry<String, Value> entry : all.entrySet()) {
            messages.add(new MessageAndType(
                    EntryChangeMessage.TYPE,
                    new EntryChangeMessage(entry.getKey(), entry.getValue())
            ));
        }

        return Optional.of(messages);
    }

    private void handleNewEntry(NewEntryMessage message) {
        mLogger.debug("Handling message: New entry {}", message.getEntryPath());
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.createEntry(path, EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION));
    }

    private void handleEntryChange(EntryChangeMessage message) {
        mLogger.debug("Handling message: Update entry {}", message.getEntryPath());
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.setEntryValue(path, message.getValue(),
                EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION, StorageOpFlag.FORCE_CHANGE));
    }

    private void handleEntryClear(EntryClearMessage message) {
        mLogger.debug("Handling message: Clear entry {}", message.getEntryPath());
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.clearEntryValue(path, EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION));
    }

    private void handleStorageContents(StorageContentsMessage message) {
        mLogger.debug("Handling message: Storage contents update");
        mStorage.setAll(message.getEntries());
    }

    private void handleEntryDelete(EntryDeleteMessage message) {
        mLogger.debug("Handling message: Delete entry {}", message.getEntryPath());
        StoragePath path = StoragePath.create(message.getEntryPath());
        mStorage.deleteEntry(path, EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION));
    }

    private void handleRequestContent(RequestContentMessage message) {
        mLogger.debug("Handling message: Storage contents update request");
        Map<String, Value> entries = mStorage.getAll();
        mWriteQueue.add(new PendingWriteMessage(StorageContentsMessage.TYPE, new StorageContentsMessage(entries)));
    }
}
