package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetServerChannel;

import java.nio.channels.SelectionKey;
import java.util.Iterator;

class ServerUpdateImpl implements NetServerChannel.Update {

    UpdateType mType;
    NetClient mUpdatedClient;
    Iterator<SelectionKey> mSelectorIterator;

    @Override
    public UpdateType getType() {
        return mType;
    }

    @Override
    public NetClient getUpdatedClient() {
        return mUpdatedClient;
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
        mUpdatedClient = null;
        mSelectorIterator = null;
    }
}
