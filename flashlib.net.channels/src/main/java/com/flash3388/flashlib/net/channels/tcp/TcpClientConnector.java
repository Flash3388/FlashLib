package com.flash3388.flashlib.net.channels.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class TcpClientConnector implements NetChannelConnector {

    private final int mBindPort;
    private final Logger mLogger;

    private NetChannel mChannel;
    private SocketChannel mBaseChannel;

    private boolean mLastAttemptError;

    public TcpClientConnector(int bindPort, Logger logger) {
        mBindPort = bindPort;
        mLogger = logger;

        mChannel = null;
        mBaseChannel = null;
        mLastAttemptError = false;
    }

    public TcpClientConnector(Logger logger) {
        this(-1, logger);
    }

    @Override
    public NetChannel connect(SocketAddress remote, Time timeout) throws IOException, TimeoutException, InterruptedException {
        if (mLastAttemptError) {
            mLastAttemptError = false;

            // wait before retrying
            Thread.sleep(timeout.valueAsMillis());
        }

        SocketChannel channel = openChannel();

        try {
            if (!channel.connect(remote)) {
                while (!channel.finishConnect()) {
                    //noinspection BusyWait
                    Thread.sleep(timeout.valueAsMillis());
                }
            }
        } catch (IOException e) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
            mLastAttemptError = true;
            throw e;
        } catch (InterruptedException e) {
            Closeables.silentClose(mBaseChannel);
            mBaseChannel = null;
            throw e;
        }

        mLogger.debug("TCP Client {} connected to {}",
                mBaseChannel.getLocalAddress(),
                mBaseChannel.getRemoteAddress());
        return new TcpChannel(mBaseChannel);
    }

    @Override
    public void close() throws IOException {
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

    private SocketChannel openChannel() throws IOException {
        if (mBaseChannel == null) {
            try {
                mLogger.debug("Opening new TCP CLIENT socket in non-blocking mode");

                mBaseChannel = SocketChannel.open();
                mBaseChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                mBaseChannel.configureBlocking(false);

                if (mBindPort >= 0) {
                    SocketAddress bindAddress = new InetSocketAddress(mBindPort);
                    mLogger.debug("Binding TCP Client to {}", bindAddress);
                    mBaseChannel.bind(bindAddress);
                }
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        return mBaseChannel;
    }
}
