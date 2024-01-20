package com.flash3388.flashlib.net.channels.udp;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpChannel implements NetChannel {

    private final DatagramChannel mChannel;
    private final SocketAddress mRemoteAddress;

    public UdpChannel(DatagramChannel channel, SocketAddress remoteAddress) {
        mChannel = channel;
        mRemoteAddress = remoteAddress;
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        SocketAddress remote = mChannel.receive(buffer);
        if (remote == null) {
            return new IncomingData(null, 0);
        }
        if (remote.equals(mChannel.getLocalAddress())) {
            return new IncomingData(null, 0);
        }

        return new IncomingData(remote, buffer.position());
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        mChannel.send(buffer, mRemoteAddress);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
