package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;

class ConnectedSendableSession {

    private SendableSession mSession;
    private SendableData mSendableData;
    private SendableData mRemoteSendableData;

    ConnectedSendableSession(SendableSession sendableSession, SendableData sendableData, SendableData remoteSendableData) {
        mSession = sendableSession;
        mSendableData = sendableData;
        mRemoteSendableData = remoteSendableData;
    }

    public SendableSession getSession() {
        return mSession;
    }

    public SendableData getSendableData() {
        return mSendableData;
    }

    public SendableData getRemoteSendableData() {
        return mRemoteSendableData;
    }
}
