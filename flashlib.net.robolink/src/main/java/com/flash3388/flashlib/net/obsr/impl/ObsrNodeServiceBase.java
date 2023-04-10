package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.PendingWriteMessage;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryDeleteMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.RequestContentMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import com.flash3388.flashlib.net.util.NetServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class ObsrNodeServiceBase extends NetServiceBase implements ObjectStorage {

    protected static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    protected final BlockingQueue<PendingWriteMessage> mWriteQueue;
    protected final Storage mStorage;

    public ObsrNodeServiceBase(InstanceId ourId, Clock clock) {
        super(ourId, clock);

        mWriteQueue = new LinkedBlockingQueue<>();

        StorageListener listener = new StorageListenerImpl(mWriteQueue, LOGGER);
        mStorage = new StorageImpl(listener, clock);
    }

    @Override
    public StoredObject getRoot() {
        return mStorage.getObject(StoragePath.root());
    }

    @Override
    public StoredObject getInstanceRoot() {
        return getRoot().getChild("instances").getChild(mOurId.toString());
    }

    protected static KnownMessageTypes getMessageTypes() {
        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(NewEntryMessage.TYPE);
        messageTypes.put(EntryClearMessage.TYPE);
        messageTypes.put(EntryChangeMessage.TYPE);
        messageTypes.put(StorageContentsMessage.TYPE);
        messageTypes.put(EntryDeleteMessage.TYPE);
        messageTypes.put(RequestContentMessage.TYPE);

        return messageTypes;
    }
}
