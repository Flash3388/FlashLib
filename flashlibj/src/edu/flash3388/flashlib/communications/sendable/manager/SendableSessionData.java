package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;

public class SendableSessionData {

    private final SendableData mLocal;
    private final SendableData mRemote;
    private final SendableSession mSendableSession;

    public SendableSessionData(SendableData local, SendableData remote, SendableSession sendableSession) {
        mLocal = local;
        mRemote = remote;
        mSendableSession = sendableSession;
    }

    public SendableData getLocal() {
        return mLocal;
    }

    public SendableData getRemote() {
        return mRemote;
    }

    public SendableSession getSession() {
        return mSendableSession;
    }
}
