package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairFailureMessage extends SendableManagerMessage {

    public static final int HEADER = 5003;

    public PairFailureMessage(SendableData from, SendableData to, PrimitiveSerializer serializer) {
        super(HEADER, from, to, serializer);
    }

    public static PairFailureMessage fromMessage(Message message, PrimitiveSerializer primitiveSerializer) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairFailureMessage) SendableManagerMessage.fromMessage(message, primitiveSerializer);
    }
}
