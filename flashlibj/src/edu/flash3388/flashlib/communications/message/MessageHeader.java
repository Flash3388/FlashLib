package edu.flash3388.flashlib.communications.message;

public class MessageHeader {

    private final int mVersion;
    private final int mMessageLength;


    public MessageHeader(int version, int messageLength) {
        mVersion = version;
        mMessageLength = messageLength;
    }

    public int getVersion() {
        return mVersion;
    }

    public int getMessageLength() {
        return mMessageLength;
    }
}
