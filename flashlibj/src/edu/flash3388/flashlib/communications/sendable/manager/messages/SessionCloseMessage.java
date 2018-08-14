package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;
import edu.flash3388.flashlib.io.PrimitiveSerializer;

public class SessionCloseMessage extends SendableManagerMessage {

    public static final int HEADER = 5004;

    public SessionCloseMessage(SendableData from, SendableData to, PrimitiveSerializer serializer) {
        super(HEADER, from, to, serializer);
    }

    public static SessionCloseMessage fromMessage(Message message, PrimitiveSerializer primitiveSerializer) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (SessionCloseMessage) SendableManagerMessage.fromMessage(message, primitiveSerializer);
    }
}
