package com.flash3388.flashlib.net.channels.nio;

import com.flash3388.flashlib.net.channels.BaseChannel;

public interface ChannelOpenListener<T extends BaseChannel> {

    void onOpen(T channel, UpdateRegistration registration);
    void onError(Throwable t);
}
