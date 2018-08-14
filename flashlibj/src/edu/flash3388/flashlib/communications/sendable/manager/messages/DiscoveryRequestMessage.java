package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.Message;

public class DiscoveryRequestMessage implements Message {

    public static final int HEADER = 7001;

    @Override
    public int getHeader() {
        return HEADER;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
