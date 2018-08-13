package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class PairSuccessMessage extends SendableManagerMessage {

    private static final int HEADER = 5002;

    public PairSuccessMessage(SendableData from, SendableData to, PrimitiveSerializer serializer) {
        super(HEADER, from, to, serializer);
    }

    public static PairSuccessMessage fromMessage(Message message, PrimitiveSerializer primitiveSerializer) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairSuccessMessage) SendableManagerMessage.fromMessage(message, primitiveSerializer);
    }
}
