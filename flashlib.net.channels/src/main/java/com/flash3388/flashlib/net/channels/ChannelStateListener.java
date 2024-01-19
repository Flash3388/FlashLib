package com.flash3388.flashlib.net.channels;

public interface ChannelStateListener {

    void onConnect(NetClientInfo clientInfo);
    void onDisconnect(NetClientInfo clientInfo);
}
