package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryValueObservableProperty;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StorageBasedEntry;
import com.flash3388.flashlib.net.obsr.StorageBasedObject;
import com.flash3388.flashlib.net.obsr.StorageOpFlag;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueProperty;
import com.flash3388.flashlib.net.obsr.ValueType;
import com.flash3388.flashlib.time.Clock;
import com.notifier.Controllers;
import com.notifier.EventController;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StorageImpl implements Storage {

    private final StorageListener mListener;
    private final Clock mClock;

    private final EventController mEventController;
    private final Map<String, StoredEntryNode> mEntries;
    private final Lock mLock;

    public StorageImpl(StorageListener listener, Clock clock) {
        mListener = listener;
        mClock = clock;

        mEventController = Controllers.newSyncExecutionController();
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
    public void setAll(Map<String, Value> values) {
        mLock.lock();
        try {
            EnumSet<StorageOpFlag> flags = EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION, StorageOpFlag.FORCE_CHANGE);
            for (Map.Entry<String, Value> entry : values.entrySet()) {
                setEntryValue(StoragePath.create(entry.getKey()),
                        entry.getValue(),
                        flags);
            }
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
    public void createEntry(StoragePath path, EnumSet<StorageOpFlag> flags) {
        mLock.lock();
        try {
            // will force create
            getOrCreateEntryNode(path, !flags.contains(StorageOpFlag.NO_REMOTE_NOTIFICATION));
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
    public void setEntryValue(StoragePath path, Value value, EnumSet<StorageOpFlag> flags) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            Value currentValue = node.getValue();
            if (currentValue.getType() != ValueType.EMPTY &&
                    value.getType() != currentValue.getType() &&
                    !flags.contains(StorageOpFlag.FORCE_CHANGE)) {
                throw new IllegalStateException("cannot change type of non-empty entry");
            }

            node.setValue(value, mClock.currentTime());

            if (mListener != null && !flags.contains(StorageOpFlag.NO_REMOTE_NOTIFICATION)) {
                mListener.onEntryUpdate(path, value);
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void clearEntryValue(StoragePath path, EnumSet<StorageOpFlag> flags) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            node.setValue(new Value(), mClock.currentTime());

            if (mListener != null && !flags.contains(StorageOpFlag.NO_REMOTE_NOTIFICATION)) {
                mListener.onEntryClear(path);
            }
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

    private StoredEntryNode getOrCreateEntryNode(StoragePath path) {
        return getOrCreateEntryNode(path, true);
    }

    private StoredEntryNode getOrCreateEntryNode(StoragePath path, boolean allowRemoteNotification) {
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

            if (mListener != null && allowRemoteNotification) {
                mListener.onNewEntry(path);
            }
        }


        return node;
    }
}