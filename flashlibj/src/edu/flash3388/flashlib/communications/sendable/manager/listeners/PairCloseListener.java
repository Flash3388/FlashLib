package edu.flash3388.flashlib.communications.sendable.manager.listeners;

import edu.flash3388.flashlib.communications.message.MessageQueue;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.manager.handlers.PairHandler;
import edu.flash3388.flashlib.communications.sendable.manager.messages.PairCloseMessage;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairCloseListener implements MessageListener {

    private PairHandler mPairHandler;
    private PrimitiveSerializer mSerializer;

    public PairCloseListener(PairHandler pairHandler, PrimitiveSerializer serializer) {
        mPairHandler = pairHandler;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        PairCloseMessage pairCloseMessage = PairCloseMessage.fromMessage(e.getMessage(), mSerializer);
        SendableData remote = pairCloseMessage.getFrom();
        SendableData local = pairCloseMessage.getTo();

        MessageQueue messageQueue = e.getMessageQueue();

        mPairHandler.unpair(local, remote, messageQueue);
    }
}
