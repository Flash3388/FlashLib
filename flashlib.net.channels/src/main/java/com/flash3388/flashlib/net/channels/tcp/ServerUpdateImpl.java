package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.ServerUpdate;

import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.Iterator;

class ServerUpdateImpl implements ServerUpdate {

    UpdateType mType;
    SocketAddress mClientAddress;
    Iterator<SelectionKey> mSelectorIterator;

    @Override
    public UpdateType getType() {
        return mType;
    }

    @Override
    public SocketAddress getClientAddress() {
        return mClientAddress;
    }

    @Override
    public void done() {
        if (mSelectorIterator != null) {
            mSelectorIterator.remove();
        }

        clear();
    }

    public void clear() {
        mType = UpdateType.NONE;
        mClientAddress = null;
        mSelectorIterator = null;
    }
}
