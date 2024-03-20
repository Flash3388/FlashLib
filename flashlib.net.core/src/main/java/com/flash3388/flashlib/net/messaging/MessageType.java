package com.flash3388.flashlib.net.messaging;

import com.castle.util.function.ThrowingFunction;
import com.flash3388.flashlib.util.function.ThrowingBiConsumer;

import java.io.DataInput;
import java.io.DataOutput;
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
     * Read the given stream into message data.
     *
     * @return message from the stream.
     * @throws IOException if an I/O error occurs.
     */
    Message read(DataInput dataInput) throws IOException;

    /**
     * Write the given message into a given stream.
     *
     * @param message message to write. Must be a message of the type represented by this object,
     *                incompatible message will lead to undefined behaviour.
     * @param dataOutput stream to write into.
     * @throws IOException if an I/O error occurs.
     */
    void write(Message message, DataOutput dataOutput) throws IOException;


    static <T extends Message> MessageType create(int key,
                              ThrowingFunction<DataInput, T, IOException> readFunction,
                              ThrowingBiConsumer<T, DataOutput, IOException> writeFunction) {
        return new Impl<>(key, readFunction, writeFunction);
    }

    class Impl<T extends Message> implements MessageType {

        private final int mKey;
        private final ThrowingFunction<DataInput, T, IOException> mReadFunction;
        private final ThrowingBiConsumer<T, DataOutput, IOException> mWriteFunction;

        public Impl(int key,
                    ThrowingFunction<DataInput, T, IOException> readFunction,
                    ThrowingBiConsumer<T, DataOutput, IOException> writeFunction) {
            mKey = key;
            mReadFunction = readFunction;
            mWriteFunction = writeFunction;
        }

        @Override
        public int getKey() {
            return mKey;
        }

        @Override
        public Message read(DataInput dataInput) throws IOException {
            return mReadFunction.apply(dataInput);
        }

        @Override
        public void write(Message message, DataOutput dataOutput) throws IOException {
            //noinspection unchecked
            T t = (T) message;
            mWriteFunction.consume(t, dataOutput);
        }
    }
}
