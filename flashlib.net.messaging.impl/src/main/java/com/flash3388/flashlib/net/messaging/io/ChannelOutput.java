package com.flash3388.flashlib.net.messaging.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ChannelOutput extends ByteArrayOutputStream {

    private final TcpChannel mChannel;

    public ChannelOutput(TcpChannel channel) {
        mChannel = channel;
    }

    @Override
    public void close() throws IOException {
        super.close();
        mChannel.write(toByteArray());
    }
}
