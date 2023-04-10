package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.InMessage;
import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import org.apache.commons.io.input.buffer.CircularByteBuffer;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MessageReadingContext {

    public static class ParseResult {

        private final MessageHeader mHeader;
        private final MessageInfo mInfo;
        private final InMessage mMessage;

        public ParseResult(MessageHeader header, MessageInfo info, InMessage message) {
            mHeader = header;
            mInfo = info;
            mMessage = message;
        }

        public MessageHeader getHeader() {
            return mHeader;
        }

        public MessageInfo getInfo() {
            return mInfo;
        }

        public InMessage getMessage() {
            return mMessage;
        }
    }

    private final KnownMessageTypes mMessageTypes;
    private final Logger mLogger;

    private final CircularByteBuffer mBuffer;
    private final byte[] mMessageHeaderBuffer;

    private MessageHeader mLastHeader;

    public MessageReadingContext(KnownMessageTypes messageTypes, Logger logger) {
        mMessageTypes = messageTypes;
        mLogger = logger;
        mBuffer = new CircularByteBuffer(2048);
        mMessageHeaderBuffer = new byte[MessageHeader.SIZE];

        mLastHeader = null;
    }

    public void clear() {
        mBuffer.clear();
    }

    public boolean hasEnoughSpace() {
        return mBuffer.hasSpace(1024);
    }

    public void updateBuffer(ByteBuffer buffer, int bytesInBuffer) {
        if (bytesInBuffer < 1) {
            return;
        }

        byte[] data = new byte[bytesInBuffer];
        buffer.get(data);

        mLogger.trace("Updating context with new data");
        mBuffer.add(data, 0, data.length);
    }

    public Optional<ParseResult> parse() throws IOException {
        // run parsing loop to handle multiple phases of parsing
        while (true) {
            if (mLastHeader == null) {
                // need to read header
                if (mBuffer.getCurrentNumberOfBytes() >= MessageHeader.SIZE) {
                    mLogger.trace("Enough bytes to read header");
                    mLastHeader = readHeader();
                } else {
                    break;
                }
            } else {
                if (mBuffer.getCurrentNumberOfBytes() >= mLastHeader.getContentSize()) {
                    mLogger.trace("Enough bytes to read message");
                    try {
                        ParseResult result = readMessage(mLastHeader);
                        mLastHeader = null;

                        return Optional.of(result);
                    } catch (IOException e) {
                        if (e.getCause() != null && e.getCause().getClass().equals(NoSuchElementException.class)) {
                            // ignore this message and inform
                            mLogger.warn("Received unknown message. Ignoring it");
                            mLastHeader = null;
                        } else {
                            throw e;
                        }
                    }
                } else {
                    break;
                }
            }
        }

        return Optional.empty();
    }

    private MessageHeader readHeader() throws IOException {
        mBuffer.read(mMessageHeaderBuffer, 0, mMessageHeaderBuffer.length);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mMessageHeaderBuffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return new MessageHeader(dataInputStream);
        }
    }

    private ParseResult readMessage(MessageHeader header) throws IOException {
        byte[] buffer = new byte[header.getContentSize()];
        mBuffer.read(buffer, 0, buffer.length);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            MessageInfo messageInfo = new MessageInfo(dataInputStream, mMessageTypes);
            InMessage message = messageInfo.getType().parse(dataInputStream);

            return new ParseResult(header, messageInfo, message);
        }
    }
}
