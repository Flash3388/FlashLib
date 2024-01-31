package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import org.slf4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MessageReadingContext {

    private static final int MAX_ALLOWED_READ_BUFFER_CAP = 8192;
    private static final int START_READ_BUFFER_CAP = 1024;

    public static class ParseResult {

        private final MessageHeader mHeader;
        private final Message mMessage;

        public ParseResult(MessageHeader header, Message message) {
            mHeader = header;
            mMessage = message;
        }

        public MessageHeader getHeader() {
            return mHeader;
        }

        public Message getMessage() {
            return mMessage;
        }
    }

    private final KnownMessageTypes mMessageTypes;
    private final Logger mLogger;

    private ByteBuffer mDirectReadBuffer;
    private byte[] mReadBuffer;
    private MessageHeader mLastHeader;

    public MessageReadingContext(KnownMessageTypes messageTypes, Logger logger) {
        mMessageTypes = messageTypes;
        mLogger = logger;

        mDirectReadBuffer = ByteBuffer.allocateDirect(START_READ_BUFFER_CAP);
        mReadBuffer = new byte[START_READ_BUFFER_CAP];

        mLastHeader = null;
    }

    public void clear() {
        mLogger.trace("Resetting message reading context");

        mDirectReadBuffer.clear();
        mLastHeader = null;
    }

    public IncomingData readFromChannel(NetChannel channel) throws IOException {
        mLogger.trace("Updating context with new data");

        IncomingData data = channel.read(mDirectReadBuffer);
        if (data == null) {
            return null;
        }

        if (!mDirectReadBuffer.hasRemaining()) {
            mLogger.debug("read buffer is full, expanding it");

            int newCapacity = mDirectReadBuffer.capacity() * 2;
            if (newCapacity >= MAX_ALLOWED_READ_BUFFER_CAP) {
                throw new IllegalStateException("buffer capacity has reached its limit");
            }

            ByteBuffer newBuffer = ByteBuffer.allocateDirect(newCapacity);
            newBuffer.put(mDirectReadBuffer);

            mDirectReadBuffer = newBuffer;
        }

        return data;
    }

    public Optional<ParseResult> parse() throws IOException {
        if (mLastHeader == null) {
            // need to read header
            if (mDirectReadBuffer.position() >= MessageHeader.SIZE) {
                mLogger.trace("Enough bytes to read header");
                mLastHeader = readHeader();
            } else {
                // not enough bytes to read header, so try again later when we have enough
                return Optional.empty();
            }
        }

        if (mDirectReadBuffer.position() >= mLastHeader.getContentSize()) {
            mLogger.trace("Enough bytes to read message");
            try {
                ParseResult result = readMessage(mLastHeader);
                return Optional.of(result);
            } catch (NoSuchElementException e) {
                // ignore this message and inform
                mLogger.warn("Received unknown message. Ignoring it");
            } finally {
                mLastHeader = null;
            }
        }

        // either failure to read/parse the message
        // or not enough data to parse it yet.
        return Optional.empty();
    }

    private MessageHeader readHeader() throws IOException {
        mDirectReadBuffer.flip();
        mDirectReadBuffer.get(mReadBuffer, 0, MessageHeader.SIZE);
        mDirectReadBuffer.compact();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mReadBuffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return new MessageHeader(dataInputStream);
        }
    }

    private ParseResult readMessage(MessageHeader header) throws IOException {
        if (header.getContentSize() > mReadBuffer.length) {
            int newCapacity = Math.max(header.getContentSize(), mReadBuffer.length * 2);
            if (newCapacity >= MAX_ALLOWED_READ_BUFFER_CAP) {
                throw new IllegalStateException("buffer capacity has reached its limit");
            }

            mReadBuffer = new byte[newCapacity];
        }

        mDirectReadBuffer.flip();
        mDirectReadBuffer.get(mReadBuffer, 0, header.getContentSize());
        mDirectReadBuffer.compact();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mReadBuffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            MessageType type = mMessageTypes.get(header.getMessageType());
            Message message = type.read(dataInputStream);

            return new ParseResult(header, message);
        }
    }
}
