package com.flash3388.flashlib.net.channels.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetClientInfo;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.channels.ServerUpdate;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TcpServerChannel implements NetServerChannel {

    private final SocketAddress mBindAddress;
    private final Logger mLogger;

    private final ServerUpdateImpl mServerUpdate;
    private ServerSocketChannel mUnderlyingChannel;
    private Selector mSelector;
    private Iterator<SelectionKey> mLastSelectionKeyIterator;

    public TcpServerChannel(SocketAddress bindAddress, Logger logger) {
        mBindAddress = bindAddress;
        mLogger = logger;

        mServerUpdate = new ServerUpdateImpl();
        mServerUpdate.clear();

        mUnderlyingChannel = null;
        mSelector = null;
        mLastSelectionKeyIterator = null;
    }

    @Override
    public ServerUpdate readNextUpdate(Time timeout) throws IOException, TimeoutException {
        if (mServerUpdate.getType() != ServerUpdate.UpdateType.NONE) {
            throw new IllegalStateException("last update was not handled completely");
        }

        openChannelIfNecessary();

        Iterator<SelectionKey> iterator;
        iterator = mLastSelectionKeyIterator;

        // try and process any old, remaining events before trying to select again.
        if (processUpdatesFromSelector(iterator, mServerUpdate)) {
            return mServerUpdate;
        }

        mLastSelectionKeyIterator = null;
        iterator = getNewUpdatesFromSelector(timeout);
        if (iterator == null) {
            return mServerUpdate;
        }

        mLastSelectionKeyIterator = iterator;
        if (!processUpdatesFromSelector(iterator, mServerUpdate)) {
            mLastSelectionKeyIterator = null;
        }

        return mServerUpdate;
    }

    @Override
    public NetClient acceptNewClient() throws IOException {
        SocketChannel baseChannel = mUnderlyingChannel.accept();
        try {
            baseChannel.configureBlocking(false);
            baseChannel.register(mSelector, SelectionKey.OP_READ);

            SocketAddress address = baseChannel.getRemoteAddress();
            NetClientInfo clientInfo = new NetClientInfo(address);
            NetChannel channel = new TcpChannel(baseChannel);

            return new NetClientImpl(clientInfo, channel);
        } catch (IOException | RuntimeException | Error e) {
            Closeables.silentClose(baseChannel);
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        // this is also used to close the channel after an error and re-open it.

        if (mUnderlyingChannel != null) {
            Closeables.silentClose(mUnderlyingChannel);
            mUnderlyingChannel = null;
        }

        if (mSelector != null) {
            Closeables.silentClose(mSelector);
            mSelector = null;
        }

        mLastSelectionKeyIterator = null;
        mServerUpdate.clear();
    }

    private synchronized void openChannelIfNecessary() throws IOException {
        if (mUnderlyingChannel == null) {
            try {
                mLogger.debug("Opening new TCP SERVER socket in non-blocking mode");

                mSelector = Selector.open();

                mUnderlyingChannel = ServerSocketChannel.open();
                mUnderlyingChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
                mUnderlyingChannel.configureBlocking(false);

                mLogger.debug("Binding TCP SERVER to {}", mBindAddress);
                mUnderlyingChannel.bind(mBindAddress);

                mUnderlyingChannel.register(mSelector, SelectionKey.OP_ACCEPT);
            } catch (IOException | RuntimeException | Error e) {
                mLogger.error("Error while opening server channel", e);
                close();
                throw e;
            }
        }
    }

    private Iterator<SelectionKey> getNewUpdatesFromSelector(Time timeout) throws IOException, TimeoutException {
        // TODO: WHEN TO THROW TIMEOUTEXCEPTION
        int available = mSelector.select(timeout.valueAsMillis());
        if (available < 1) {
            return null;
        }

        return mSelector.selectedKeys().iterator();
    }

    private boolean processUpdatesFromSelector(Iterator<SelectionKey> iterator, ServerUpdateImpl serverUpdate) {
        if (iterator == null) {
            return false;
        }

        boolean tryAgain;
        do {
            if (!iterator.hasNext()) {
                return false;
            }

            SelectionKey key = iterator.next();
            try {
                if (key.isAcceptable()) {
                    // new client
                    mLogger.trace("Select registered accept");
                    serverUpdate.mType = ServerUpdate.UpdateType.NEW_CLIENT;
                } else if (key.isReadable()) {
                    // new data from client
                    mLogger.trace("Select registered read");

                    //noinspection resource
                    SocketAddress address = ((SocketChannel) key.channel()).getRemoteAddress();
                    serverUpdate.mType = ServerUpdate.UpdateType.NEW_DATA;
                    serverUpdate.mClientAddress = address;
                }

                tryAgain = false;
            } catch (IOException | RuntimeException | Error e) {
                mLogger.error("Error while reading updates in server channel", e);
                serverUpdate.clear();
                tryAgain = true;
            }
        } while (tryAgain);

        serverUpdate.mSelectorIterator = iterator;

        return true;
    }
}
