package com.flash3388.flashlib.net.data;

import com.flash3388.flashlib.net.Remote;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemotesStorageImpl implements InternalRemoteStorage {

    private final Map<String, InternalRemote> mRemotesById;
    private final Lock mLock;

    public RemotesStorageImpl() {
        mRemotesById = new HashMap<>();
        mLock = new ReentrantLock();
    }

    @Override
    public Optional<Remote> getById(String id) {
        mLock.lock();
        try {
            InternalRemote remote = mRemotesById.get(id);
            if (remote == null) {
                return Optional.empty();
            }

            return Optional.of(remote);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public InternalRemote getOrCreateRemote(String id) {
        mLock.lock();
        try {
            InternalRemote remote = mRemotesById.get(id);
            if (remote == null) {
                remote = new RemoteImpl(id);
                mRemotesById.put(id, remote);
            }

            return remote;
        } finally {
            mLock.unlock();
        }
    }
}
