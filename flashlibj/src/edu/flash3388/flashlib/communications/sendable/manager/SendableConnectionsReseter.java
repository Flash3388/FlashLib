package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.SendableData;

import java.util.Map;
import java.util.function.Consumer;

class SendableConnectionsReseter implements Runnable {

    private Map<Integer, AttachedSendable> mSendables;
    private Consumer<SendableData> mDisconnector;

    SendableConnectionsReseter(Map<Integer, AttachedSendable> sendables, Consumer<SendableData> sendableDisconnector) {
        mSendables = sendables;
        mDisconnector = sendableDisconnector;
    }

    @Override
    public void run() {
        for (AttachedSendable attachedSendable : mSendables.values()) {
            mDisconnector.accept(attachedSendable.getSendableData());
        }
    }
}
