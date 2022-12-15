package com.flash3388.flashlib.hmi.comm;

public interface BasicMessage {

    MessageType getType();
    byte[] getContent();

    class Impl implements BasicMessage {
        private final MessageType mType;
        private final byte[] mContent;

        public Impl(MessageType type, byte[] content) {
            mType = type;
            mContent = content;
        }

        public MessageType getType() {
            return mType;
        }

        public byte[] getContent() {
            return mContent;
        }
    }
}
