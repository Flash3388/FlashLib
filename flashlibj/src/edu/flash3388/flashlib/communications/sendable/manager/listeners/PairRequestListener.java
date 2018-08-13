package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.PairHandler;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairRequestMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairRequestListener implements MessageListener {

    private PairHandler mPairHandler;
    private PrimitiveSerializer mSerializer;

    public PairRequestListener(PairHandler pairHandler, PrimitiveSerializer serializer) {
        mPairHandler = pairHandler;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        PairRequestMessage pairRequestMessage = PairRequestMessage.fromMessage(e.getMessage(), mSerializer);
        SendableData remote = pairRequestMessage.getFrom();
        SendableData local = pairRequestMessage.getTo();

        MessageQueue messageQueue = e.getMessageQueue();

        mPairHandler.pair(local, remote, messageQueue);
    }
}
