package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpChannel implements NetChannel {

    private final SocketChannel mChannel;
    private final SocketAddress mRemoteAddress;

    public TcpChannel(SocketChannel channel) throws IOException {
        mChannel = channel;
        mRemoteAddress = channel.getRemoteAddress();
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        int received = mChannel.read(buffer);
        return new IncomingData(mRemoteAddress, received);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        mChannel.write(buffer);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
