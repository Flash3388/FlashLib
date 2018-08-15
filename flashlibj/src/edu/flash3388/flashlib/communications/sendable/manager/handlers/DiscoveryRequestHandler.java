package edu.flash3388.flashlib.communications.sendable.manager.handlers;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.SendableStorage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryDataMessage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairRequestMessage;

import java.util.Collection;

public class DiscoveryRequestHandler implements ManagerMessageHandler {

    private final SendableStorage mSendableStorage;

    public DiscoveryRequestHandler(SendableStorage sendableStorage) {
        mSendableStorage = sendableStorage;
    }

    @Override
    public boolean canHandle(Message message) {
        return message.getHeader() == PairRequestMessage.HEADER;
    }

    @Override
    public void handle(Message message, MessageQueue messageQueue) {
        Collection<SendableData> sendableDataCollection = mSendableStorage.getAllSendables();

        messageQueue.enqueueMessage(new DiscoveryDataMessage(DiscoveryDataMessage.State.ATTACHED, sendableDataCollection));
    }
}
