package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;

import java.io.IOException;
import java.nio.ByteBuffer;

class NetClientImpl implements NetClient {

    private final NetClientInfo mClientInfo;
    private final NetChannel mChannel;

    public NetClientImpl(NetClientInfo clientInfo, NetChannel channel) {
        mClientInfo = clientInfo;
        mChannel = channel;
    }

    @Override
    public NetClientInfo getInfo() {
        return mClientInfo;
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
