package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;

public class TcpServerChannelOpener implements NetChannelOpener<NetServerChannel> {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;

    public TcpServerChannelOpener(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;
    }

    @Override
    public NetServerChannel open() throws IOException {
        return new TcpServerChannel(mBindAddress, mLogger);
    }
}
