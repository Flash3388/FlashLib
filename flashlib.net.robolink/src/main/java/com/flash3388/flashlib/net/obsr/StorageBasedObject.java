package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;

public class StorageBasedObject implements StoredObject {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;

    public StorageBasedObject(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
    }

    @Override
    public StoredObject getChild(String name) {
        if (name.indexOf(StoragePath.DELIMITER) > 0) {
            throw new IllegalArgumentException("bad name, cannot contain " + StoragePath.DELIMITER);
        }

        StoragePath childPath = mPath.child(name + StoragePath.DELIMITER);

        Storage storage = getStorage();
        return storage.getObject(childPath);
    }

    @Override
    public StoredEntry getEntry(String name) {
        if (name.indexOf(StoragePath.DELIMITER) > 0) {
            throw new IllegalArgumentException("bad name, cannot contain " + StoragePath.DELIMITER);
        }

        StoragePath childPath = mPath.child(name);

        Storage storage = getStorage();
        return storage.getEntry(childPath);
    }

    @Override
    public void addListener(ObjectListener listener) {
        Storage storage = getStorage();
        storage.addListener(mPath, listener);
    }

    @Override
    public void removeListener(ObjectListener listener) {
        Storage storage = getStorage();
        storage.removeListener(mPath, listener);
    }

    @Override
    public String toString() {
        return String.format("{OBJECT %s}", mPath);
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }
}
