package com.flash3388.flashlib.net.channels;

public interface RemoteConfigurableChannel extends NetChannel {

    void setRemote(NetAddress info);
}
