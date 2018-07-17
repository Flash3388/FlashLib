package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.communications.message.event.MessageListener;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableCreator;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.Pair;

import java.util.Map;
import java.util.function.Consumer;

class SendablePairRequestListener implements MessageListener {

    private Map<Integer, Pair<SendableData, Sendable>> mSendables;
    private Consumer<SendableData> mSendableConnector;
    private SendableCreator mSendableCreator;
    private PrimitiveSerializer mSerializer;

    SendablePairRequestListener(Consumer<SendableData> sendableConnector, PrimitiveSerializer serializer) {
        mSendableConnector = sendableConnector;
        mSerializer = serializer;
    }

    @Override
    public void onMessageReceived(MessageEvent e) {
        Message message = e.getMessage();
        SendableData sendableData = SendableData.fromMessage(message, mSerializer);


    }
}
