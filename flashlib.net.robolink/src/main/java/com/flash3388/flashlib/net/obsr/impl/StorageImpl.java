package com.flash3388.flashlib.net.obsr.impl;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageMetadata;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;
import com.flash3388.flashlib.net.obsr.EntryListener;
import com.flash3388.flashlib.net.obsr.EntryModificationEvent;
import com.flash3388.flashlib.net.obsr.ModificationType;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StorageBasedEntry;
import com.flash3388.flashlib.net.obsr.StorageBasedObject;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueProperty;
import com.flash3388.flashlib.net.obsr.impl.messages.EntryChangeMessage;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StorageImpl implements Storage {

    private final StorageListener mListener;
    private final Clock mClock;
    private final EventController mEventController;
    private final Logger mLogger;

    private final Map<String, StoredEntryNode> mEntries;
    private final Lock mLock;

    public StorageImpl(StorageListener listener, Clock clock, EventController eventController, Logger logger) {
        mListener = listener;
        mClock = clock;
        mEventController = eventController;
        mLogger = logger;

        mEntries = new HashMap<>();
        mLock = new ReentrantLock();
    }

    @Override
    public Map<String, Value> getAll() {
        mLock.lock();
        try {
            Map<String, Value> result = new HashMap<>();
            for (Map.Entry<String, StoredEntryNode> entry : mEntries.entrySet()) {
                StoredEntryNode node = entry.getValue();
                result.put(entry.getKey(), node.getValue());
            }

            return result;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public StoredObject getObject(StoragePath path) {
        mLock.lock();
        try {
            StoredEntryNode entry = mEntries.get(path.toString());
            if (entry != null) {
                throw new IllegalArgumentException("path represents an object");
            }

            return new StorageBasedObject(path, this);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void deleteObject(StoragePath path) {
        mLock.lock();
        try {
            removeAllInHierarchy(path);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public RegisteredListener addListener(StoragePath path, EntryListener listener) {
        mLock.lock();
        try {
            mEventController.registerListener(listener, (event)-> {
                // TODO: MAKE ACTUAL CLASS
                return event instanceof EntryModificationEvent &&
                        ((EntryModificationEvent)event).getPath().startsWith(path.toString());
            });

            return new RegisteredListenerImpl(mEventController, listener);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public StoredEntry getEntry(StoragePath path) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            return node.getEntry();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public ValueProperty getEntryValueProperty(StoragePath path) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            return node.getValueProperty();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Value getEntryValue(StoragePath path) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            return node.getValue();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setEntryValue(StoragePath path, Value value) {
        mLock.lock();
        try {
            Time now = mClock.currentTime();
            updateEntryValue(path, value, now, true);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clearEntryValue(StoragePath path) {
        mLock.lock();
        try {
            Time now = mClock.currentTime();
            clearEntryValue(path, now, true);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void deleteEntry(StoragePath path) {
        mLock.lock();
        try {
            Time now = mClock.currentTime();
            deleteEntry(path, now, true);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void updateFromMessage(NewMessageEvent event) {
        MessageType type = event.getType();

        if (type.equals(EntryChangeMessage.TYPE)) {
            EntryChangeMessage message = event.getMessage(EntryChangeMessage.class);
            mLogger.debug("Handling message: Update entry {}", message.getEntryPath());

            mLock.lock();
            try {
                handleEntryChangeMessage(event.getMetadata(), message);
            } finally {
                mLock.unlock();
            }
        }
    }

    private void handleEntryChangeMessage(MessageMetadata metadata, EntryChangeMessage message) {
        StoragePath path = StoragePath.create(message.getEntryPath());
        Time changeTime = metadata.getTimestamp();

        if ((message.getFlags() & EntryChangeMessage.FLAG_CLEARED) != 0) {
            clearEntryValue(path, changeTime, false);
        } else if ((message.getFlags() & EntryChangeMessage.FLAG_DELETED) != 0) {
            deleteEntry(path, changeTime, false);
        } else {
            updateEntryValue(path, message.getValue(), changeTime, false);
        }
    }

    private void updateEntryValue(StoragePath path, Value value, Time changeTime, boolean shouldReport) {
        StoredEntryNode node = getOrCreateEntryNode(path);
        if (node.getLastChangeTimestamp().after(changeTime)) {
            return;
        }

        node.setValue(value, changeTime);

        if (shouldReport && mListener != null) {
            mListener.onEntryUpdate(path, value);
        }

        mEventController.fire(
                new EntryModificationEvent(
                        node.getEntry(),
                        node.getEntry().getPath().toString(),
                        value,
                        ModificationType.UPDATE),
                EntryModificationEvent.class,
                EntryListener.class,
                EntryListener::onEntryModification);
    }

    private void clearEntryValue(StoragePath path, Time changeTime, boolean shouldReport) {
        StoredEntryNode node = getOrCreateEntryNode(path);
        if (node.getLastChangeTimestamp().after(changeTime)) {
            return;
        }

        node.setValue(Value.empty(), changeTime);

        if (shouldReport && mListener != null) {
            mListener.onEntryClear(path);
        }

        mEventController.fire(
                new EntryModificationEvent(
                        node.getEntry(),
                        node.getEntry().getPath().toString(),
                        Value.empty(),
                        ModificationType.CLEAR),
                EntryModificationEvent.class,
                EntryListener.class,
                EntryListener::onEntryModification);
    }

    private void deleteEntry(StoragePath path, Time changeTime, boolean shouldReport) {
        String strPath = path.toString();
        StoredEntryNode node = mEntries.get(strPath);
        if (node == null) {
            // never existed
            return;
        }

        if (node.getLastChangeTimestamp().after(changeTime)) {
            return;
        }

        mEntries.remove(strPath);

        if (shouldReport && mListener != null) {
            mListener.onEntryDeleted(path);
        }

        mEventController.fire(
                new EntryModificationEvent(
                        null,
                        path.toString(),
                        Value.empty(),
                        ModificationType.DELETE),
                EntryModificationEvent.class,
                EntryListener.class,
                EntryListener::onEntryModification);
    }

    private void removeAllInHierarchy(StoragePath rootPath) {
        for (String path : new HashSet<>(mEntries.keySet())) {
            if (StoragePath.create(path).startsWith(rootPath)) {
                deleteEntry(StoragePath.create(path));
            }
        }
    }

    private StoredEntryNode getOrCreateEntryNode(StoragePath path) {
        String strPath = path.toString();

        StoredEntryNode node = mEntries.get(strPath);
        if (node == null) {
            StorageBasedEntry entry = new StorageBasedEntry(path, this);
            EntryValueObservableProperty valueProperty = new EntryValueObservableProperty(
                    this,
                    path,
                    mEventController,
                    entry);
            node = new StoredEntryNode(entry, valueProperty);
            mEntries.put(strPath, node);
        }

        return node;
    }
}
