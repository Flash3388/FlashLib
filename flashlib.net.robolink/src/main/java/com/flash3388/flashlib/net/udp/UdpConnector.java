package com.flash3388.flashlib.net.udp;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.ConnectedNetChannel;
import com.flash3388.flashlib.net.NetConnector;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.Map;

public class UdpConnector implements NetConnector {

    private final SocketAddress mBindAddress;
    private final Map<SocketOption<?>, Object> mSocketOptions;
    private final Logger mLogger;

    private DatagramChannel mBaseChannel;
    private ConnectedUdpChannel mChannel;
    private boolean mLastAttemptError;

    public UdpConnector(SocketAddress bindAddress, Map<SocketOption<?>, Object> socketOptions, Logger logger) {
        mBindAddress = bindAddress;
        mSocketOptions = socketOptions;
        mLogger = logger;
        mBaseChannel = null;
        mChannel = null;
        mLastAttemptError = false;
    }

    public UdpConnector(SocketAddress bindAddress, Logger logger) {
        this(bindAddress, new HashMap<>(), logger);
    }

    @Override
    public ConnectedNetChannel connect(SocketAddress remote, Time timeout) throws IOException, InterruptedException {
        if (mLastAttemptError) {
            mLastAttemptError = false;

            // wait before retrying
            Thread.sleep(timeout.valueAsMillis());
        }

        DatagramChannel channel = openChannel();

        try {
            channel.connect(remote);
        } catch (IOException e) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
            mLastAttemptError = true;
            throw e;
        }

        try {
            mChannel = new ConnectedUdpChannel(mBaseChannel);
            return mChannel;
        } catch (IOException e) {
            mLastAttemptError = true;
            close();
            throw e;
        }
    }

    @Override
    public void close() {
        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
            mBaseChannel = null;
        } else if (mBaseChannel != null) {
            Closeables.silentClose(mBaseChannel);
            mChannel = null;
            mBaseChannel = null;
        }
    }

    private DatagramChannel openChannel() throws IOException {
        if (mBaseChannel == null) {
            try {
                mLogger.debug("Opening new UDP socket in non-blocking mode");

                mBaseChannel = DatagramChannel.open();
                mBaseChannel.configureBlocking(false);
                mBaseChannel.bind(mBindAddress);

                for (Map.Entry<SocketOption<?>, Object> entry : mSocketOptions.entrySet()) {
                    //noinspection unchecked,rawtypes
                    mBaseChannel.setOption((SocketOption) entry.getKey(), entry.getValue());
                }
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        return mBaseChannel;
    }
}
