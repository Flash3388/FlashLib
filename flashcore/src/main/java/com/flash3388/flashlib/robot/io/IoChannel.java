package com.flash3388.flashlib.robot.io;

public interface IoChannel {

    static <T extends IoChannel> T cast(IoChannel channel, Class<T> type) {
        if (type.isInstance(channel)) {
            return type.cast(channel);
        }

        throw new ClassCastException(String.format("Channel is not of type %s", type.getName()));
    }
}
