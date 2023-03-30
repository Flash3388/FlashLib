package com.flash3388.flashlib.net.channels.tcp;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public class TcpRoutingService extends SingleUseService {

    private static final Logger LOGGER = Logging.getLogger("Comm", "TCPRoutingService");

    private final SocketAddress mBindAddress;

    private final AtomicReference<TcpServerChannel> mChannel;
    private Thread mThread;

    public TcpRoutingService(SocketAddress bindAddress) {
        mBindAddress = bindAddress;
        mChannel = new AtomicReference<>();
        mThread = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        TcpServerChannel channel = new TcpServerChannel(mBindAddress, LOGGER,
                new UpdateHandler(mChannel, LOGGER));
        mChannel.set(channel);

        mThread = new Thread(
                new ReadTask(mChannel.get(), LOGGER),
                "TcpRoutingServer-Task");
        mThread.setDaemon(true);

        mThread.start();
    }

    @Override
    protected void stopRunning() {
        mThread.interrupt();
        mThread = null;

        Closeables.silentClose(mChannel.getAndSet(null));
    }

    private static class ReadTask implements Runnable {

        private final TcpServerChannel mChannel;
        private final Logger mLogger;

        private ReadTask(TcpServerChannel channel, Logger logger) {
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.processUpdates();
                } catch (Throwable t) {
                    mLogger.error("Error in processing updates", t);
                }
            }
        }
    }

    private static class UpdateHandler implements NetServerChannel.UpdateHandler {

        private final AtomicReference<TcpServerChannel> mChannel;
        private final Logger mLogger;

        private UpdateHandler(AtomicReference<TcpServerChannel> channel, Logger logger) {
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void onClientConnected(NetClientInfo clientInfo) {

        }

        @Override
        public void onClientDisconnected(NetClientInfo clientInfo) {

        }

        @Override
        public void onNewData(NetClientInfo sender, ByteBuffer buffer, int amountReceived) {
            try {
                mLogger.debug("Routing server received data from {}", sender.getAddress());

                buffer.flip();
                TcpServerChannel channel = mChannel.get();
                channel.writeToAllBut(buffer, sender);
            } catch (IOException e) {
                mLogger.error("Failed to route message", e);
            }
        }
    }
}
