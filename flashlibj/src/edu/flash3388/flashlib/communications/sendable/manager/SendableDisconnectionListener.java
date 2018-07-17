package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

import java.util.function.Consumer;

class SendableDisconnectionListener implements MessageListener {

    private Consumer<SendableData> mSendableDisconnector;
    private PrimitiveSerializer mSerializer;

    SendableDisconnectionListener(Consumer<SendableData> sendableDisconnector, PrimitiveSerializer serializer) {
        mSendableDisconnector = sendableDisconnector;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Message message = e.getMessage();
        SendableData sendableData = SendableData.fromMessage(message, mSerializer);

        mSendableDisconnector.accept(sendableData);
    }
}
