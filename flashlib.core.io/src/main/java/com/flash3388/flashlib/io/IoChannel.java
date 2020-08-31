package com.flash3388.flashlib.io;

/**
 * Represents a connection to an io port. Used with {@link IoInterface}
 * as a key for identifying specific devices.
 *
 * The actual object to create depends on the implementation used for the API. Allowing
 * each implementation to define different requirements for the channel.
 *
 * @since FlashLib 3.0.0
 */
public interface IoChannel {

    /**
     * Casts the {@link IoChannel} to the requested type.
     * <p>
     *     Should be used by implementations in {@link IoInterface} to cast the given
     *     object to the wanted type.
     * </p>
     * <pre>
     *     void newChannel(IoChannel channel) {
     *         MyIoChannel myChannel = IoChannel.cast(channel, MyIoChannel.class);
     *         myChannel.getData();
     *     }
     * </pre>
     *
     * @param channel the channel object.
     * @param type the wanted class type.
     * @param <T> type parameter indicating the wanted class.
     *
     * @return a casted {@link IoChannel}.
     *
     * @throws ClassCastException if the given instance cannot be casted to the wanted type.
     */
    static <T extends IoChannel> T cast(IoChannel channel, Class<T> type) {
        if (type.isInstance(channel)) {
            return type.cast(channel);
        }

        throw new ClassCastException(String.format("Channel is not of type %s", type.getName()));
    }

    class Stub implements IoChannel {
    }
}
