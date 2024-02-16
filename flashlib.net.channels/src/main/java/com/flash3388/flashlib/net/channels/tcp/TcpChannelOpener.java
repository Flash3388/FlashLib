package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.ConnectableNetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;

public class TcpChannelOpener implements NetChannelOpener<ConnectableNetChannel> {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;

    public TcpChannelOpener(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;
    }

    public TcpChannelOpener(Logger logger) {
        this(null, logger);
    }

    @Override
    public boolean isTargetChannelStreaming() {
        return true;
    }

    @Override
    public ConnectableNetChannel open() throws IOException {
        return new TcpChannel(mBindAddress, mLogger);
    }
}
