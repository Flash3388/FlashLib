package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.beans.ValueSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

class SendableDisconnector implements Consumer<SendableData> {

    private Map<Integer, ConnectedSendableSession> mSendableSessions;
    private ValueSource<MessageWriter> mMessageWriterSource;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    SendableDisconnector(Map<Integer, ConnectedSendableSession> sendableSessions, ValueSource<MessageWriter> messageWriterSource,
                         PrimitiveSerializer serializer, Logger logger) {
        mSendableSessions = sendableSessions;
        mMessageWriterSource = messageWriterSource;
        mSerializer = serializer;
        mLogger = logger;
    }

    @Override
    public void accept(SendableData sendableData) {
        int id = sendableData.getId();

        if (!mSendableSessions.containsKey(id)) {
            mLogger.warning("Cannot close sendable with no session: " + id);
            return;
        }

        ConnectedSendableSession connectedSendableSession = mSendableSessions.get(id);
        SendableSession sendableSession = connectedSendableSession.getSession();
        sendableSession.close();
        mSendableSessions.remove(id);

        MessageWriter writer = mMessageWriterSource.getValue();
        if (writer == null) {
            mLogger.warning("MessageWriter is null");
            return;
        }

        Message disconnectionMessage = new SendableConnectionMessage(SendableMessageHeader.SENDABLE_DISCONNECTION_HEADER,
                connectedSendableSession.getSendableData(), connectedSendableSession.getRemoteSendableData(), mSerializer);
        try {
            writer.writeMessage(disconnectionMessage);
        } catch (WriteException e) {
            mLogger.log(Level.SEVERE, "Failed to send message", e);
        }
    }
}
