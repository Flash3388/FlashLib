package com.flash3388.flashlib.net.data;

import com.flash3388.flashlib.net.RemotesStorage;

public interface InternalRemoteStorage extends RemotesStorage {

    InternalRemote getOrCreateRemote(String id);
}
