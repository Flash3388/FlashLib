package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.Pair;

import java.util.function.Consumer;

class SendableConnectionListener implements MessageListener {

    private Consumer<Pair<SendableData, SendableData>> mSendableConnector;
    private PrimitiveSerializer mSerializer;

    SendableConnectionListener(Consumer<Pair<SendableData, SendableData>> sendableConnector, PrimitiveSerializer serializer) {
        mSendableConnector = sendableConnector;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Message message = e.getMessage();
        SendableConnectionMessage connectionMessage = SendableConnectionMessage.fromMessage(message, mSerializer);

        SendableData from = connectionMessage.getFrom();
        SendableData to = connectionMessage.getTo();

        mSendableConnector.accept(Pair.create(from, to));
    }
}
