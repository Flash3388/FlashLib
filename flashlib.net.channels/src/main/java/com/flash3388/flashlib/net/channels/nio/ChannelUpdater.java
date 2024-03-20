package com.flash3388.flashlib.net.channels.nio;

import com.flash3388.flashlib.net.channels.BaseChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.time.Time;

import java.io.IOException;
import java.nio.channels.SelectableChannel;

public interface ChannelUpdater {

    <T extends BaseChannel> void requestOpenChannel(NetChannelOpener<? extends T> opener,
                                                    ChannelOpenListener<? super T> listener,
                                                    ChannelListener listenerToRegister,
                                                    Time delay);
    default <T extends BaseChannel> void requestOpenChannel(NetChannelOpener<? extends T> opener,
                                                            ChannelOpenListener<? super T> listener,
                                                            ChannelListener listenerToRegister) {
        requestOpenChannel(opener, listener, listenerToRegister, Time.milliseconds(0));
    }


    UpdateRegistration register(SelectableChannel channel, int ops, ChannelListener listener) throws IOException;
}
