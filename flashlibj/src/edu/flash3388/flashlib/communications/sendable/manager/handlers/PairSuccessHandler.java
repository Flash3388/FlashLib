package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairSuccessMessage;

import java.util.concurrent.locks.Lock;

public class PairSuccessHandler implements ManagerMessageHandler {

    private final PairHandler mPairHandler;
    private final Lock mManagerLock;

    public PairSuccessHandler(PairHandler pairHandler, Lock managerLock) {
        mPairHandler = pairHandler;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return PairSuccessMessage.HEADER == message.getHeader();
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        PairSuccessMessage pairSuccessMessage = PairSuccessMessage.fromMessage(message);
        SendableData remote = pairSuccessMessage.getFrom();
        SendableData local = pairSuccessMessage.getTo();

        mManagerLock.lock();
        try {
            mPairHandler.pair(local, remote, messageQueue);
        } finally {
            mManagerLock.unlock();
        }
    }
}
