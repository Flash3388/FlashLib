package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;

import java.io.IOException;
import java.nio.ByteBuffer;

class NetClientImpl implements NetClient {

    private final NetAddress mClientAddress;
    private final NetChannel mChannel;

    public NetClientImpl(NetAddress clientAddress, NetChannel channel) {
        mClientAddress = clientAddress;
        mChannel = channel;
    }

    @Override
    public NetAddress getAddress() {
        return mClientAddress;
    }

    @Override
    public UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException {
        return mChannel.register(updater, listener);
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        return mChannel.read(buffer);
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
