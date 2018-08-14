package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairSuccessMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.concurrent.locks.Lock;

public class PairSuccessHandler implements ManagerMessageHandler {

    private final PairHandler mPairHandler;
    private final PrimitiveSerializer mSerializer;
    private final Lock mManagerLock;

    public PairSuccessHandler(PairHandler pairHandler, PrimitiveSerializer serializer, Lock managerLock) {
        mPairHandler = pairHandler;
        mSerializer = serializer;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return PairSuccessMessage.HEADER == message.getHeader();
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        PairSuccessMessage pairSuccessMessage = PairSuccessMessage.fromMessage(message, mSerializer);
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
