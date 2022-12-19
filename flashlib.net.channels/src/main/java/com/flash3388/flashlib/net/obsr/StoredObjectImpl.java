package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;

public class StoredObjectImpl implements StoredObject {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;

    public StoredObjectImpl(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
    }

    @Override
    public StoredObject getChild(String name) {
        StoragePath childPath = mPath.child(name);

        Storage storage = getStorage();
        return storage.getObject(childPath);
    }

    @Override
    public StoredEntry getEntry(String name) {
        StoragePath childPath = mPath.child(name);

        Storage storage = getStorage();
        return storage.getEntry(childPath);
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }
}
