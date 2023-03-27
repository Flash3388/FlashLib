package com.flash3388.flashlib.net.obsr.impl;

import com.castle.concurrent.service.SingleUseService;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.net.obsr.messages.EntryChangeMessage;
import com.flash3388.flashlib.net.obsr.messages.EntryClearMessage;
import com.flash3388.flashlib.net.obsr.messages.NewEntryMessage;
import com.flash3388.flashlib.net.obsr.messages.StorageContentsMessage;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

public abstract class ObsrNodeServiceBase extends SingleUseService implements ObjectStorage {

    protected static final Logger LOGGER = Logging.getLogger("Comm", "OBSRNode");

    private final InstanceId mInstanceId;

    public ObsrNodeServiceBase(InstanceId ourId) {
        mInstanceId = ourId;
    }

    @Override
    public StoredObject getRoot() {
        return getStorage().getObject(StoragePath.root());
    }

    @Override
    public StoredObject getInstanceRoot() {
        return getRoot().getChild("instances").getChild(mInstanceId.toString());
    }

    protected abstract Storage getStorage();

    protected static KnownMessageTypes getMessageTypes() {
        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(NewEntryMessage.TYPE);
        messageTypes.put(EntryClearMessage.TYPE);
        messageTypes.put(EntryChangeMessage.TYPE);
        messageTypes.put(StorageContentsMessage.TYPE);

        return messageTypes;
    }
}
