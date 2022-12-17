package com.flash3388.flashlib.net.packets;

public interface Packet {

    int getContentType();
    byte[] getContent();

    static Packet create(int contentType, byte[] content) {
        return new Impl(contentType, content);
    }

    class Impl implements Packet {

        private final int mContentType;
        private final byte[] mContent;

        public Impl(int contentType, byte[] content) {
            mContentType = contentType;
            mContent = content;
        }

        @Override
        public int getContentType() {
            return mContentType;
        }

        @Override
        public byte[] getContent() {
            return mContent;
        }
    }
}
