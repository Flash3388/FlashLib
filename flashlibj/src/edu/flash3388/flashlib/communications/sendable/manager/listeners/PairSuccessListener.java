package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.PairHandler;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairSuccessMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairSuccessListener implements MessageListener {

    private PairHandler mPairHandler;
    private PrimitiveSerializer mSerializer;

    public PairSuccessListener(PairHandler pairHandler, PrimitiveSerializer serializer) {
        mPairHandler = pairHandler;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        PairSuccessMessage pairSuccessMessage = PairSuccessMessage.fromMessage(e.getMessage(), mSerializer);
        SendableData remote = pairSuccessMessage.getFrom();
        SendableData local = pairSuccessMessage.getTo();

        MessageQueue messageQueue = e.getMessageQueue();

        mPairHandler.pair(local, remote, messageQueue);
    }
}
