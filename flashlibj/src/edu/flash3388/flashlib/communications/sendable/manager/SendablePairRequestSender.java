package edu.flash3388.flashlib.communications.sendable.manager;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.message.MessageWriter;
import edu.flash3388.flashlib.communications.message.WriteException;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;
import edu.flash3388.flashlib.util.beans.ValueSource;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.flash3388.flashlib.communications.sendable.manager.SendableMessageHeader.SENDABLE_PAIR_REQUEST_HEADER;

class SendablePairRequestSender implements Consumer<SendableData> {

    private PrimitiveSerializer mSerializer;
    private ValueSource<MessageWriter> mMessageWriterSource;
    private Logger mLogger;

    SendablePairRequestSender(PrimitiveSerializer serializer, ValueSource<MessageWriter> messageWriterSource, Logger logger) {
        mSerializer = serializer;
        mMessageWriterSource = messageWriterSource;
        mLogger = logger;
    }

    @Override
    public void accept(SendableData sendableData) {
        MessageWriter messageWriter = mMessageWriterSource.getValue();
        if (messageWriter == null) {
            mLogger.warning("Message writer missing");
            return;
        }

        Message message = sendableData.toMessage(SENDABLE_PAIR_REQUEST_HEADER, mSerializer);
        try {
            messageWriter.writeMessage(message);
        } catch (WriteException e) {
            mLogger.log(Level.SEVERE, "failed to write pair request message", e);
        }
    }
}
