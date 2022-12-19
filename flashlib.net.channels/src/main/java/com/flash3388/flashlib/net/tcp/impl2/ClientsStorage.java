package com.flash3388.flashlib.net.tcp.impl2;

import com.flash3388.flashlib.net.ConnectedNetChannel;
import com.flash3388.flashlib.net.tcp.ConnectedTcpChannel;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientsStorage {

    private static class Node {
        public final ConnectedTcpChannel channel;

        private Node(ConnectedTcpChannel channel) {
            this.channel = channel;
        }
    }

    private final Map<SocketAddress, ConnectedNetChannel> mAddressToNode;
    private final Lock mLock;

    public ClientsStorage() {
        mAddressToNode = new HashMap<>();
        mLock = new ReentrantLock();
    }

    public Map<SocketAddress, ConnectedNetChannel> getAll() {
        mLock.lock();
        try {
            return new HashMap<>(mAddressToNode);
        } finally {
            mLock.unlock();
        }
    }

    public ConnectedNetChannel getChannelByAddress(SocketAddress address) {
        mLock.lock();
        try {
            return mAddressToNode.get(address);
        } finally {
            mLock.unlock();
        }
    }

    public void putChannel(SocketAddress address, ConnectedNetChannel channel) {
        mLock.lock();
        try {
            mAddressToNode.put(address, channel);
        } finally {
            mLock.unlock();
        }
    }

    public ConnectedNetChannel remoteByAddress(SocketAddress address) {
        mLock.lock();
        try {
            return mAddressToNode.remove(address);
        } finally {
            mLock.unlock();
        }
    }

    public void removeAll(Collection<SocketAddress> addresses) {
        mLock.lock();
        try {
            for (SocketAddress address : addresses) {
                //noinspection resource
                mAddressToNode.remove(address);
            }
        } finally {
            mLock.unlock();
        }
    }
}
