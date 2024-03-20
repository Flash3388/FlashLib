package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;

public class ChannelData {

    public final NetChannel channel;
    public final UpdateRegistration registration;
    public boolean readyForWriting;

    public ChannelData(NetChannel channel, UpdateRegistration registration) {
        this.channel = channel;
        this.registration = registration;
        this.readyForWriting = false;
    }
}
