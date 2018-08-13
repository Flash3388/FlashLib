package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairCloseMessage extends SendableManagerMessage {

    private static final int HEADER = 5004;

    public PairCloseMessage(SendableData from, SendableData to, PrimitiveSerializer serializer) {
        super(HEADER, from, to, serializer);
    }

    public static PairCloseMessage fromMessage(Message message, PrimitiveSerializer primitiveSerializer) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairCloseMessage) SendableManagerMessage.fromMessage(message, primitiveSerializer);
    }
}
