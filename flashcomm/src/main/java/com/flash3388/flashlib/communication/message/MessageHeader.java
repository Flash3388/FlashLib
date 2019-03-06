package com.flash3388.flashlib.communication.message;

import com.flash3388.flashlib.util.versioning.Version;

import java.io.Serializable;

public class MessageHeader implements Serializable {

    private final Version mVersion;
    private final int mMessageLength;

    public MessageHeader(Version version, int messageLength) {
        mVersion = version;
        mMessageLength = messageLength;
    }

    public Version getVersion() {
        return mVersion;
    }

    public int getMessageLength() {
        return mMessageLength;
    }
}