package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.MessageType;
import org.apache.commons.io.input.buffer.CircularByteBuffer;
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

    private final CircularByteBuffer mDataToReadBuffer;
    private final byte[] mReadBuffer;

    private MessageHeader mLastHeader;

    public MessageReadingContext(KnownMessageTypes messageTypes, Logger logger) {
        mMessageTypes = messageTypes;
        mLogger = logger;
        // TODO: USE THIS OR SOME OTHER BUFFER FOR BOTH READING FROM THE CHANNEL AND PARSING
        mDataToReadBuffer = new CircularByteBuffer(2048);
        mReadBuffer = new byte[1024];

        mLastHeader = null;
    }

    public void clear() {
        mLogger.trace("Resetting message reading context");

        mDataToReadBuffer.clear();
        mLastHeader = null;
    }

    public void updateBuffer(ByteBuffer buffer, int bytesInBuffer) {
        if (bytesInBuffer < 1) {
            return;
        }

        mLogger.trace("Updating context with new data");

        if (!mDataToReadBuffer.hasSpace(bytesInBuffer)) {
            // our side data buffer doesn't have enough space to
            // store the additional incoming data.
            throw new BufferOverflowException();
        }

        while (bytesInBuffer > 0) {
            int bytesToRead = Math.min(bytesInBuffer, mReadBuffer.length);
            buffer.get(mReadBuffer, 0, bytesToRead);
            bytesInBuffer -= bytesToRead;

            mDataToReadBuffer.add(mReadBuffer, 0, bytesToRead);
        }
    }

    public Optional<ParseResult> parse() throws IOException {
        if (mLastHeader == null) {
            // need to read header
            if (mDataToReadBuffer.getCurrentNumberOfBytes() >= MessageHeader.SIZE) {
                mLogger.trace("Enough bytes to read header");
                mLastHeader = readHeader();
            } else {
                // not enough bytes to read header, so try again later when we have enough
                return Optional.empty();
            }
        }

        if (mDataToReadBuffer.getCurrentNumberOfBytes() >= mLastHeader.getContentSize()) {
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
        mDataToReadBuffer.read(mReadBuffer, 0, MessageHeader.SIZE);
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

        mDataToReadBuffer.read(mReadBuffer, 0, header.getContentSize());

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(mReadBuffer);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            MessageType type = mMessageTypes.get(header.getMessageType());
            Message message = type.read(dataInputStream);

            return new ParseResult(header, message);
        }
    }
}
