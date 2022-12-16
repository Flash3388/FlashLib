package com.flash3388.flashlib.net.messaging;

import java.util.function.Supplier;

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
     * Creates a new instance of the message type represented by this type.
     *
     * @return empty message
     */
    Message create();

    /**
     * Creates a new {@link MessageType}.
     *
     * @param key unique identifier of the type
     * @param messageCreator creator for the message type identified by this.
     *
     * @return new type
     */
    static MessageType createType(int key, Supplier<? extends Message> messageCreator) {
        return new Impl(key, messageCreator);
    }

    /**
     * A basic implementation for {@link MessageType}
     */
    class Impl implements MessageType {

        private final int mKey;
        private final Supplier<? extends Message> mMessageCreator;

        public Impl(int key, Supplier<? extends Message> messageCreator) {
            mKey = key;
            mMessageCreator = messageCreator;
        }

        @Override
        public int getKey() {
            return mKey;
        }

        @Override
        public Message create() {
            return mMessageCreator.get();
        }
    }
}
