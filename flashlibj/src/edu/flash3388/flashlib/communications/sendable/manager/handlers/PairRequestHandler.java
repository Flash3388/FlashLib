package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairRequestMessage;

import java.util.concurrent.locks.Lock;

public class PairRequestHandler implements ManagerMessageHandler {

    private final PairHandler mPairHandler;
    private final Lock mManagerLock;

    public PairRequestHandler(PairHandler pairHandler, Lock managerLock) {
        mPairHandler = pairHandler;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return PairRequestMessage.HEADER == message.getHeader();
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        PairRequestMessage pairRequestMessage = PairRequestMessage.fromMessage(message);
        SendableData remote = pairRequestMessage.getFrom();
        SendableData local = pairRequestMessage.getTo();

        mManagerLock.lock();
        try {
            mPairHandler.pair(local, remote, messageQueue);
        } finally {
            mManagerLock.unlock();
        }
    }
}
