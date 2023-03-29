package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.function.ThrowingFunction;

import java.io.DataInput;
import java.io.IOException;

/**
 * Represents a type of message.
 * Used to transmit the message and parse it.
 *
 * Each type is represented by an integer key, which has to be unique for identification
 * across connections.
 *
 * @since FlashLib 3.2.0
 */
public interface MessageType {

    /**
     * Unique identifier of the type.
     *
     * @return key
     */
    int getKey();

    /**
     * Parse the given stream into message data.
     * @return parsed message
     */
    Message parse(DataInput dataInput) throws IOException;


    static MessageType create(int key, ThrowingFunction<DataInput, ? extends Message, IOException> parseFunction) {
        return new Impl(key, parseFunction);
    }

    class Impl implements MessageType {

        private final int mKey;
        private final ThrowingFunction<DataInput, ? extends Message, IOException> mParseFunction;

        public Impl(int key, ThrowingFunction<DataInput, ? extends Message, IOException> parseFunction) {
            mKey = key;
            mParseFunction = parseFunction;
        }

        @Override
        public int getKey() {
            return mKey;
        }

        @Override
        public Message parse(DataInput dataInput) throws IOException {
            return mParseFunction.apply(dataInput);
        }
    }
}
