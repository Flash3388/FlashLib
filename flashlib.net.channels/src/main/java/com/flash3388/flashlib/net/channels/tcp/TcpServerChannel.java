package com.flash3388.flashlib.net.channels.tcp;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class TcpServerChannel implements NetServerChannel {

    private final Logger mLogger;

    private final ServerSocketChannel mChannel;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger) throws IOException {
        mLogger = logger;
        mChannel = openChannel(bindAddress);
    }

    @Override
    public UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException {
        return updater.register(mChannel, SelectionKey.OP_ACCEPT, listener);
    }

    @Override
    public NetClient acceptNewClient() throws IOException {
        SocketChannel baseChannel = mChannel.accept();
        try {
            baseChannel.configureBlocking(false);

            SocketAddress address = baseChannel.getRemoteAddress();
            NetClientInfo clientInfo = new NetClientInfo(address);
            NetChannel channel = new ConnectedTcpChannel(baseChannel);

            return new NetClientImpl(clientInfo, channel);
        } catch (IOException | RuntimeException | Error e) {
            Closeables.silentClose(baseChannel);
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        Closeables.silentClose(mChannel);
    }

    private ServerSocketChannel openChannel(SocketAddress bindAddress) throws IOException {
        mLogger.debug("Opening new TCP SERVER socket in non-blocking mode");
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.configureBlocking(false);

            mLogger.debug("Binding TCP SERVER to {}", bindAddress);
            channel.bind(bindAddress);

            return channel;
        } catch (IOException | RuntimeException | Error e) {
            mLogger.error("Error while opening server channel", e);

            if (channel != null) {
                Closeables.silentClose(channel);
            }

            throw e;
        }
    }
}
