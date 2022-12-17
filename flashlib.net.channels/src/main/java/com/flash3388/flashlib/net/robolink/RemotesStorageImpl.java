package com.flash3388.flashlib.net.robolink;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RemotesStorageImpl<T extends Remote> implements RemotesStorage {

    private static class RemoteNode<T> {
        public final T remote;
        public SocketAddress address;

        private RemoteNode(T remote) {
            this.remote = remote;
            address = null;
        }
    }

    private final Map<SocketAddress, String> mRemotesIdByAddress;
    private final Map<String, RemoteNode<T>> mRemotesById;
    private final Lock mLock;

    public RemotesStorageImpl() {
        mRemotesIdByAddress = new HashMap<>();
        mRemotesById = new HashMap<>();
        mLock = new ReentrantLock();
    }

    public Optional<T> updateRemoteByAddressAndId(SocketAddress address, String id) {
        mLock.lock();
        try {
            RemoteNode<T> node = mRemotesById.get(id);
            if (node == null) {
                // new remote!
                return Optional.empty();
            }

            if (!address.equals(node.address)) {
                // address updated
                node.address = address;
                mRemotesIdByAddress.put(address, id);
            }

            return Optional.of(node.remote);
        } finally {
            mLock.unlock();
        }
    }

    public void putNewRemote(SocketAddress address, T t) {
        mLock.lock();
        try {
            RemoteNode<T> node = new RemoteNode<>(t);
            node.address = address;
            mRemotesById.put(t.getId(), node);

            mRemotesIdByAddress.put(address, t.getId());
        } finally {
            mLock.unlock();
        }
    }

    public Optional<SocketAddress> getAddressForId(String id) {
        mLock.lock();
        try {
            RemoteNode<T> node = mRemotesById.get(id);
            if (node == null) {
                return Optional.empty();
            }

            return Optional.of(node.address);
        } finally {
            mLock.unlock();
        }
    }

    public Collection<SocketAddress> getAllRemoteAddress() {
        mLock.lock();
        try {
            return new HashSet<>(mRemotesIdByAddress.keySet());
        } finally {
            mLock.unlock();
        }
    }

    public void clear() {
        mLock.lock();
        try {
            mRemotesById.clear();
            mRemotesIdByAddress.clear();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Optional<Remote> getById(String id) {
        mLock.lock();
        try {
            RemoteNode<T> node = mRemotesById.get(id);
            if (node == null) {
                return Optional.empty();
            }

            return Optional.of(node.remote);
        } finally {
            mLock.unlock();
        }
    }
}
