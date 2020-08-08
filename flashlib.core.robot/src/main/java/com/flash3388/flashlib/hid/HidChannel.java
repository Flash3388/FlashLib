package com.flash3388.flashlib.hid;

public interface HidChannel {

    static <T extends HidChannel> T cast(HidChannel channel, Class<T> type) {
        if (type.isInstance(channel)) {
            return type.cast(channel);
        }

        throw new ClassCastException(String.format("Channel is not of type %s", type.getName()));
    }

    class Stub implements HidChannel {
    }
}
