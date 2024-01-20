package com.flash3388.flashlib.net.channels.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class TcpClientConnector implements NetChannelConnector {

    private static final int MIN_CONNECTION_WAIT_TIME_MS = 5;

    private final int mBindPort;
    private final Clock mClock;
    private final Logger mLogger;

    public TcpClientConnector(int bindPort, Clock clock, Logger logger) {
        mBindPort = bindPort;
        mClock = clock;
        mLogger = logger;
    }

    public TcpClientConnector(Clock clock, Logger logger) {
        this(-1, clock, logger);
    }

    @Override
    public NetChannel connect(SocketAddress remote, Time timeout) throws IOException, TimeoutException, InterruptedException {
        Time startTime = mClock.currentTime();
        Time sleepTime = Time.milliseconds(Math.max(MIN_CONNECTION_WAIT_TIME_MS, timeout.valueAsMillis() / 4));

        SocketChannel channel = openChannel();

        try {
            if (!channel.connect(remote)) {
                while (!channel.finishConnect()) {
                    // TODO: IS IT POSSIBLE TO RE-USE THIS CHANNEL INSTEAD OF CREATING ANOTHER?
                    Time now = mClock.currentTime();
                    if (now.sub(startTime).largerThanOrEquals(timeout)) {
                        throw new TimeoutException();
                    }

                    //noinspection BusyWait
                    Thread.sleep(sleepTime.valueAsMillis());
                }
            }
        } catch (IOException | TimeoutException | InterruptedException e) {
            Closeables.silentClose(channel);
            throw e;
        }

        mLogger.debug("TCP Client {} connected to {}",
                channel.getLocalAddress(),
                channel.getRemoteAddress());

        return new TcpChannel(channel);
    }

    @Override
    public void close() throws IOException {

    }

    private SocketChannel openChannel() throws IOException {
        SocketChannel channel = null;
        try {
            mLogger.debug("Opening new TCP CLIENT socket in non-blocking mode");

            channel = SocketChannel.open();
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.configureBlocking(false);

            if (mBindPort >= 0) {
                SocketAddress bindAddress = new InetSocketAddress(mBindPort);
                mLogger.debug("Binding TCP Client to {}", bindAddress);
                channel.bind(bindAddress);
            }
        } catch (IOException e) {
            if (channel != null) {
                Closeables.silentClose(channel);
            }

            throw e;
        }

        return channel;
    }
}
