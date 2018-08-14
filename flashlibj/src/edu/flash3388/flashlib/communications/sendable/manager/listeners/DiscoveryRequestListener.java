package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.SendableStorage;
import edu.flash3388.flashlib.communications.sendable.manager.messages.DiscoveryDataMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.Collection;

public class DiscoveryRequestListener implements MessageListener {

    private SendableStorage mSendableStorage;
    private PrimitiveSerializer mSerializer;

    public DiscoveryRequestListener(SendableStorage sendableStorage, PrimitiveSerializer serializer) {
        mSendableStorage = sendableStorage;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Collection<SendableData> sendableDataCollection = mSendableStorage.getAllSendables();

        MessageQueue messageQueue = e.getMessageQueue();
        messageQueue.enqueueMessage(new DiscoveryDataMessage(DiscoveryDataMessage.State.ATTACHED, sendableDataCollection, mSerializer));
    }
}
