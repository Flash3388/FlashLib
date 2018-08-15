package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;

public class SessionCloseMessage extends SendableManagerMessage {

    public static final int HEADER = 5004;

    public SessionCloseMessage(SendableData from, SendableData to) {
        super(HEADER, from, to);
    }

    public static SessionCloseMessage fromMessage(Message message) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (SessionCloseMessage) SendableManagerMessage.fromMessage(message);
    }
}
