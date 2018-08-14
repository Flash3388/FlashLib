package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;

import java.util.HashMap;
import java.util.Map;

public class SendableStorage {

    private SendableStreamFactory mSendableStreamFactory;
    private Map<SendableData, SendableController> mSendableControllers;

    public SendableStorage(SendableStreamFactory sendableStreamFactory) {
        mSendableStreamFactory = sendableStreamFactory;
        mSendableControllers = new HashMap<SendableData, SendableController>();
    }

    public synchronized boolean addSendable(SendableData sendableData, Sendable sendable) {
        if(mSendableControllers.containsKey(sendableData)) {
            return false;
        }

        mSendableControllers.put(sendableData, new SendableController(sendableData, sendable, mSendableStreamFactory));
        return true;
    }

    public synchronized boolean removeSendable(SendableData sendableData) {
        return mSendableControllers.remove(sendableData) != null;
    }

    public SendableController getControllerForSendable(SendableData sendableData) throws NoSuchSendableException {
        SendableController controller;

        synchronized (this) {
            controller = mSendableControllers.get(sendableData);
        }

        if (controller == null) {
            throw new NoSuchSendableException();
        }

        return controller;
    }
}
