package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;
import edu.flash3388.flashlib.communications.sendable.SendableData;

public class PairRequestMessage extends SendableManagerMessage {

    public static final int HEADER = 5001;

    public PairRequestMessage(SendableData from, SendableData to) {
        super(HEADER, from, to);
    }

    public static PairRequestMessage fromMessage(Message message) {
        if (message.getHeader() != HEADER) {
            throw new IllegalArgumentException("given message doesn't have a matching header: " + message.getHeader());
        }

        return (PairRequestMessage) SendableManagerMessage.fromMessage(message);
    }
}
