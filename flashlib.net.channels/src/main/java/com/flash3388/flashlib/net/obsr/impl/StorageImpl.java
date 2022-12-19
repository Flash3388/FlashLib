package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.BasicEntry;
import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.StorageBasedEntry;
import com.flash3388.flashlib.net.obsr.EntryValueType;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StorageOpFlag;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.StoredObjectImpl;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StorageImpl implements Storage {

    private final StorageListener mListener;
    private final Clock mClock;
    private final Logger mLogger;

    // TODO: STORAGE OBJECTS AS WELL?
    private final Map<String, StoredEntryNode> mEntries;
    private final Lock mLock; // TODO: ReadWriteLock? StampedLock?

    public StorageImpl(StorageListener listener, Clock clock, Logger logger) {
        mListener = listener;
        mClock = clock;
        mLogger = logger;

        mEntries = new HashMap<>();
        mLock = new ReentrantLock();
    }

    @Override
    public Map<String, BasicEntry> getAll() {
        mLock.lock();
        try {
            Map<String, BasicEntry> result = new HashMap<>();
            for (Map.Entry<String, StoredEntryNode> entry : mEntries.entrySet()) {
                StoredEntryNode node = entry.getValue();
                result.put(entry.getKey(), new BasicEntry(node.getType(), node.getValue()));
            }

            return result;
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setAll(Map<String, BasicEntry> values) {
        mLock.lock();
        try {
            EnumSet<StorageOpFlag> flags = EnumSet.of(StorageOpFlag.NO_REMOTE_NOTIFICATION, StorageOpFlag.FORCE_CHANGE);
            for (Map.Entry<String, BasicEntry> entry : values.entrySet()) {
                setEntryValue(StoragePath.create(entry.getKey()),
                        entry.getValue().getType(),
                        entry.getValue().getValue(),
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
                // TODO: THROW ERROR? RETURN EMPTY? OR MAYBE SEPARATE PATHS FOR OBJECTS AND ENTRIES
                throw new IllegalStateException("path represents an object");
            }

            return new StoredObjectImpl(path, this);
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
            getOrCreateEntryNode(path);
            return new StorageBasedEntry(path, this);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public EntryValueType getEntryType(StoragePath path) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            return node.getType();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Optional<Object> getEntryValueForType(StoragePath path, EntryValueType type) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            if (node.getType() == EntryValueType.EMPTY) {
                return Optional.empty();
            }

            if (type != node.getType()) {
                // TODO: THROW ERROR? RETURN EMPTY?
                throw new IllegalStateException("wrong type");
            }

            return Optional.of(node.getValue());
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void setEntryValue(StoragePath path, EntryValueType type, Object value, EnumSet<StorageOpFlag> flags) {
        mLock.lock();
        try {
            StoredEntryNode node = getOrCreateEntryNode(path);
            if (node.getType() != EntryValueType.EMPTY &&
                    type != node.getType() &&
                    !flags.contains(StorageOpFlag.FORCE_CHANGE)) {
                // TODO: THROW ERROR? RETURN EMPTY?
                throw new IllegalStateException("wrong type");
            }

            node.setValue(type, value, mClock.currentTime(), flags.contains(StorageOpFlag.FORCE_CHANGE));

            if (mListener != null && !flags.contains(StorageOpFlag.NO_REMOTE_NOTIFICATION)) {
                mListener.onEntryUpdate(path, type, value);
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
            node.setValue(EntryValueType.EMPTY, null, mClock.currentTime(), flags.contains(StorageOpFlag.FORCE_CHANGE));

            if (mListener != null && !flags.contains(StorageOpFlag.NO_REMOTE_NOTIFICATION)) {
                mListener.onEntryClear(path);
            }
        } finally {
            mLock.unlock();
        }
    }

    private StoredEntryNode getOrCreateEntryNode(StoragePath path) {
        return getOrCreateEntryNode(path, true);
    }

    private StoredEntryNode getOrCreateEntryNode(StoragePath path, boolean allowRemoteNotification) {
        String strPath = path.toString();

        StoredEntryNode entry = mEntries.get(strPath);
        if (entry == null) {
            entry = new StoredEntryNode();
            mEntries.put(strPath, entry);

            if (mListener != null && allowRemoteNotification) {
                mListener.onNewEntry(path);
            }
        }


        return entry;
    }
}
