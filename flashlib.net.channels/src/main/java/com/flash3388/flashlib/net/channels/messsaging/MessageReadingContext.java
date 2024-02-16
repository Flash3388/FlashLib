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
import java.nio.ByteOrder;
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
    private final byte[] mMessageHeaderMagic;

    private ByteBuffer mDirectReadBuffer;
    private byte[] mReadBuffer;
    private MessageHeader mLastHeader;

    public MessageReadingContext(KnownMessageTypes messageTypes, Logger logger) {
        mMessageTypes = messageTypes;
        mLogger = logger;

        mMessageHeaderMagic = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(MessageHeader.MAGIC).array();
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
            mDirectReadBuffer.flip();
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

                mDirectReadBuffer.flip();
                boolean foundMagic = findAndMoveToNextHeader();
                if (!foundMagic) {
                    // no header found in data
                    mLogger.trace("Header magic was not found in data, ignoring data");
                    mDirectReadBuffer.compact();
                    return Optional.empty();
                }

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
                if (result != null) {
                    return Optional.of(result);
                }
            } catch (NoSuchElementException e) {
                // ignore this message and inform
                mLogger.warn("Received unknown message. Ignoring it");
            } catch (Throwable t) {
                mLogger.error("Error parsing message", t);
            } finally {
                mLastHeader = null;
            }
        }

        // either failure to read/parse the message
        // or not enough data to parse it yet.
        return Optional.empty();
    }

    private MessageHeader readHeader() throws IOException {
        mDirectReadBuffer.get(mReadBuffer, 0, MessageHeader.SIZE);
        mDirectReadBuffer.compact();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mReadBuffer, 0, MessageHeader.SIZE);
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

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mReadBuffer, 0, header.getContentSize());
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            MessageType type = mMessageTypes.get(header.getMessageType());
            Message message = type.read(dataInputStream);
            if (message == null) {
                // bad message
                return null;
            }

            return new ParseResult(header, message);
        }
    }

    private boolean findAndMoveToNextHeader() {
        // TODO: IMPROVE ALGORITHM
        // we need to search in a byte granularity, as we can't trust alignment of data received.

        mLogger.trace("Starting search for magic: firstByte={}", mMessageHeaderMagic[0]);

        byte firstByte;
        do {
            firstByte = mDirectReadBuffer.get();
            if (firstByte == mMessageHeaderMagic[0]) {
                int startPosition = mDirectReadBuffer.position() - 1;
                mLogger.trace("Found first header byte at {}", startPosition + 1);
                // possible starting position of the magic
                // return to starting position
                mDirectReadBuffer.position(startPosition);
                int value = mDirectReadBuffer.getInt();
                // return to starting position
                mDirectReadBuffer.position(startPosition);

                if (value == MessageHeader.MAGIC) {
                    mLogger.trace("Found magic at {}", startPosition + 1);
                    return true;
                }
            }
        } while (firstByte != mMessageHeaderMagic[0] && mDirectReadBuffer.hasRemaining());

        return false;
    }
}
