package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.SendableStorage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryDataMessage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairRequestMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Collection;
import java.util.concurrent.locks.Lock;

public class DiscoveryRequestHandler implements ManagerMessageHandler {

    private final SendableStorage mSendableStorage;
    private final PrimitiveSerializer mSerializer;
    private final Lock mManagerLock;

    public DiscoveryRequestHandler(SendableStorage sendableStorage, PrimitiveSerializer serializer, Lock managerLock) {
        mSendableStorage = sendableStorage;
        mSerializer = serializer;
        mManagerLock = managerLock;
    }

    @Override
    public boolean canHandle(Message message) {
        return message.getHeader() == PairRequestMessage.HEADER;
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        Collection<SendableData> sendableDataCollection = mSendableStorage.getAllSendables();

        messageQueue.enqueueMessage(new DiscoveryDataMessage(DiscoveryDataMessage.State.ATTACHED, sendableDataCollection, mSerializer));
    }
}
