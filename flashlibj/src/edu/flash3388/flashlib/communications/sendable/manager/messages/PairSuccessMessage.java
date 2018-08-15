package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;

public class PairSuccessMessage extends SendableManagerMessage {

    public static final int HEADER = 5002;

    public PairSuccessMessage(SendableData from, SendableData to) {
        super(HEADER, from, to);
    }

    public static PairSuccessMessage fromMessage(Message message) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairSuccessMessage) SendableManagerMessage.fromMessage(message);
    }
}
