package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.messages.SessionCloseMessage;

import java.util.concurrent.locks.Lock;

public class SessionCloseHandler implements ManagerMessageHandler {

    private final PairHandler mPairHandler;
    private final Lock mManagerLock;

    public SessionCloseHandler(PairHandler pairHandler, Lock managerLock) {
        mPairHandler = pairHandler;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return SessionCloseMessage.HEADER == message.getHeader();
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        SessionCloseMessage sessionCloseMessage = SessionCloseMessage.fromMessage(message);
        SendableData local = sessionCloseMessage.getTo();

        mManagerLock.lock();
        try {
            mPairHandler.unpairWithoutResponse(local);
        } finally {
            mManagerLock.unlock();
        }
    }
}
