package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;

public class PairFailureMessage extends SendableManagerMessage {

    public static final int HEADER = 5003;

    public PairFailureMessage(SendableData from, SendableData to) {
        super(HEADER, from, to);
    }

    public static PairFailureMessage fromMessage(Message message) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairFailureMessage) SendableManagerMessage.fromMessage(message);
    }
}
