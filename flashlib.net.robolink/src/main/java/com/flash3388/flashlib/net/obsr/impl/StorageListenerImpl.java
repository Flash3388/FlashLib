package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryDeleteMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;

public class StorageListenerImpl  implements StorageListener {

    private final BlockingQueue<PendingWriteMessage> mQueue;
    private final Logger mLogger;

    public StorageListenerImpl(BlockingQueue<PendingWriteMessage> queue, Logger logger) {
        mQueue = queue;
        mLogger = logger;
    }

    @Override
    public void onNewEntry(StoragePath path) {
        mLogger.debug("New entry in path {}", path);
        mQueue.add(new PendingWriteMessage(NewEntryMessage.TYPE, new NewEntryMessage(path.toString())));
    }

    @Override
    public void onEntryUpdate(StoragePath path, Value value) {
        mLogger.debug("Update to entry in path {}, value={}", path, value);
        mQueue.add(new PendingWriteMessage(EntryChangeMessage.TYPE, new EntryChangeMessage(path.toString(), value)));
    }

    @Override
    public void onEntryClear(StoragePath path) {
        mLogger.debug("Entry in path {} cleared", path);
        mQueue.add(new PendingWriteMessage(EntryClearMessage.TYPE, new EntryClearMessage(path.toString())));
    }

    @Override
    public void onEntryDeleted(StoragePath path) {
        mLogger.debug("Entry in path {} deleted", path);
        mQueue.add(new PendingWriteMessage(EntryDeleteMessage.TYPE, new EntryDeleteMessage(path.toString())));
    }
}
