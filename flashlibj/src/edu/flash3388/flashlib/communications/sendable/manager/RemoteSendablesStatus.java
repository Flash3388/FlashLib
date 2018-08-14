package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RemoteSendablesStatus {

    private Set<SendableData> mSendables;

    public RemoteSendablesStatus() {
        mSendables = new HashSet<SendableData>();
    }

    public synchronized void updateAttached(Collection<SendableData> sendableDataCollection) {
        mSendables.addAll(sendableDataCollection);
    }

    public synchronized void updateDetached(Collection<SendableData> sendableDataCollection) {
        mSendables.removeAll(sendableDataCollection);
    }

    public synchronized void reset() {
        mSendables.clear();
    }
}
