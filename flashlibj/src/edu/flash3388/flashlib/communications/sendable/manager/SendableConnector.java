package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.sendable.Sendable;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.communications.sendable.SendableSession;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.Pair;
import edu.flash3388.flashlib.util.beans.ValueSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

class SendableConnector implements Consumer<Pair<SendableData, SendableData>> {

    private Map<Integer, AttachedSendable> mSendables;
    private Map<Integer, ConnectedSendableSession> mSendableSessions;
    private ValueSource<MessageWriter> mMessageWriterSource;
    private PrimitiveSerializer mSerializer;
    private Logger mLogger;

    SendableConnector(Map<Integer, AttachedSendable> sendables, Map<Integer, ConnectedSendableSession> sendableSessions,
                      ValueSource<MessageWriter> messageWriterSource, PrimitiveSerializer serializer, Logger logger) {
        mSendables = sendables;
        mSendableSessions = sendableSessions;
        mMessageWriterSource = messageWriterSource;
        mSerializer = serializer;
        mLogger = logger;
    }

    @Override
    public void accept(Pair<SendableData, SendableData> connectPair) {
        SendableData localData = connectPair.getValue();
        SendableData remoteData = connectPair.getKey();

        int localId = localData.getId();

        if (!mSendableSessions.containsKey(localId) && mSendables.containsKey(localId)) {
            Sendable sendable = mSendables.get(localId).getSendable();
            MessageWriter messageWriter = mMessageWriterSource.getValue();

            if (messageWriter == null) {
                mLogger.warning("MessageWriter missing while connecting: " + localId);
                return;
            }

            SendableSession sendableSession = sendable.onPairing(new SendableMessageWriter(messageWriter, localData, remoteData, mSerializer));
            if (sendableSession == null) {
                mLogger.warning("SendableSession already exists for sendable: " + localId);
                return;
            }

            mSendableSessions.put(localId, new ConnectedSendableSession(sendableSession, localData, remoteData));
        }
    }
}
