package com.flash3388.flashlib.hid;

/**
 * Represents a connection to a human interface object. Used with {@link HidInterface}
 * as a key for identifying specific devices.
 *
 * The actual object to create depends on the implementation used for the API. Allowing
 * each implementation to define different requirements for the channel.
 *
 * @since FlashLib 3.0.0
 */
public interface HidChannel {

    /**
     * Casts the {@link HidChannel} to the requested type.
     * <p>
     *     Should be used by implementations in {@link HidInterface} to cast the given
     *     object to the wanted type.
     * </p>
     * <pre>
     *     void newChannel(HidChannel channel) {
     *         MyHidChannel myChannel = HidChannel.cast(channel, MyHidChannel.class);
     *         myChannel.getData();
     *     }
     * </pre>
     *
     * @param channel the channel object.
     * @param type the wanted class type.
     * @param <T> type parameter indicating the wanted class.
     *
     * @return a casted {@link HidChannel}.
     *
     * @throws ClassCastException if the given instance cannot be casted to the wanted type.
     */
    static <T extends HidChannel> T cast(HidChannel channel, Class<T> type) {
        if (type.isInstance(channel)) {
            return type.cast(channel);
        }

        throw new ClassCastException(String.format("Channel is not of type %s", type.getName()));
    }

    class Stub implements HidChannel {
    }
}
