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

    private final ByteBuffer mDirectReadBuffer;
    private final byte[] mReadBuffer;

    private MessageHeader mLastHeader;

    public MessageReadingContext(KnownMessageTypes messageTypes, Logger logger) {
        mMessageTypes = messageTypes;
        mLogger = logger;

        mDirectReadBuffer = ByteBuffer.allocateDirect(2048);
        mReadBuffer = new byte[1024];

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

        // todo: if buffer becomes too full, allocate a new bigger buffer

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
            // our reading buffer doesn't have enough space to read from the buffered data.
            // TODO: CREATE AN INPUT STREAM WHICH DYNAMICALLY FILLS FROM THE BUFFERED DATA
            throw new BufferOverflowException();
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
