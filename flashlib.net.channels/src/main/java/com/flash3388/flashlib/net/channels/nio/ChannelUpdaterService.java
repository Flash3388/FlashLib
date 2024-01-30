package com.flash3388.flashlib.net.channels.nio;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.BaseChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class ChannelUpdaterService extends SingleUseService implements ChannelUpdater {

    // TODO: INTEGRATE WITH EVENTCONTROLLER???
    // TODO: ABSTRACT SELECTION KEY???

    private static final Logger LOGGER = Logging.getLogger("Comm", "ChannelUpdater");
    private static final Time SELECT_TIMEOUT = Time.milliseconds(100);

    private final Map<SelectionKey, ChannelListener> mListeners;
    private final Queue<OpenRequest<?>> mOpenRequests;
    private final Queue<UpdateRequest> mCustomUpdateRequests;

    private Selector mSelector;
    private Thread mThread;

    public ChannelUpdaterService() {
        mListeners = new HashMap<>();
        mOpenRequests = new ArrayDeque<>();
        mCustomUpdateRequests = new ArrayDeque<>();
    }

    @Override
    public <T extends BaseChannel> void requestOpenChannel(NetChannelOpener<? extends T> opener, ChannelOpenListener<? super T> listener, ChannelListener listenerToRegister) {
        synchronized (mOpenRequests) {
            mOpenRequests.add(new OpenRequest<T>(opener, listener, listenerToRegister));
        }
    }

    @Override
    public UpdateRegistration register(SelectableChannel channel, int ops, ChannelListener listener) throws IOException {
        if (mSelector == null || !mSelector.isOpen()) {
            throw new IllegalArgumentException("service not operative");
        }

        synchronized (mListeners) {
            SelectionKey key = channel.register(mSelector, ops);
            Object old = mListeners.put(key, listener);
            if (old != null) {
                LOGGER.warn("new registered key {} already has listener {}", key, old);
            }

            return new UpdateRegistrationImpl(key, mCustomUpdateRequests);
        }
    }

    @Override
    protected void startRunning() throws ServiceException {
        try {
            mSelector = Selector.open();
            mThread = new Thread(new Task(this, LOGGER), "ChannelUpdater");
            mThread.setDaemon(true);
            mThread.start();
        } catch (IOException e) {
            throw new ServiceException(e);
        }
    }

    @Override
    protected void stopRunning() {
        mThread.interrupt();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        mThread = null;

        Closeables.silentClose(mSelector);
        mSelector = null;

        synchronized (mListeners) {
            mListeners.clear();
        }
    }

    private static class Task implements Runnable {

        private final ChannelUpdaterService mService;
        private final Logger mLogger;

        private final List<OpenRequest> mOpenRequestsLocal;
        private final List<UpdateRequest> mUpdateRequestsLocal;

        Task(ChannelUpdaterService service, Logger logger) {
            mService = service;
            mLogger = logger;

            mOpenRequestsLocal = new ArrayList<>();
            mUpdateRequestsLocal = new ArrayList<>();
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    // this may block for a while, but we are okay with that.
                    waitAndHandleUpdates();
                    processCustomUpdates();
                    processOpenRequests();
                } catch (InterruptedIOException e) {
                    mLogger.warn("Thread interrupted", e);
                    break;
                } catch (Throwable t) {
                    mLogger.error("Unknown error", t);
                }
            }
        }

        private void waitAndHandleUpdates() throws IOException {
            int updated = mService.mSelector.select(SELECT_TIMEOUT.valueAsMillis());
            if (updated < 1) {
                return;
            }

            Iterator<SelectionKey> it = mService.mSelector.selectedKeys().iterator();
            processUpdates(it);
        }

        private void processUpdates(Iterator<SelectionKey> it) {
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                ChannelListener listener;
                synchronized (mService.mListeners) {
                    if (!key.isValid()) {
                        mLogger.warn("Key {} is no longer valid, removing", key);
                        mService.mListeners.remove(key);
                    }

                    listener = mService.mListeners.get(key);
                    if (listener == null) {
                        mLogger.warn("Key {} has no attached listener", key);

                        try {
                            key.cancel();
                        } catch (Throwable ignore) {}

                        continue;
                    }
                }

                try {
                    if (key.isAcceptable()) {
                        listener.onAcceptable(key);
                    } else if (key.isConnectable()) {
                        listener.onConnectable(key);
                    } else if (key.isReadable()) {
                        listener.onReadable(key);
                    } else if (key.isWritable()) {
                        listener.onWritable(key);
                    }
                } catch (Throwable t) {
                    mLogger.error("Error while handling key {}", key, t);
                }
            }
        }

        private void processCustomUpdates() {
            mUpdateRequestsLocal.clear();
            synchronized (mService.mCustomUpdateRequests) {
                if (mService.mCustomUpdateRequests.isEmpty()) {
                    return;
                }

                mUpdateRequestsLocal.addAll(mService.mCustomUpdateRequests);
                mService.mCustomUpdateRequests.clear();
            }

            for (UpdateRequest request : mUpdateRequestsLocal) {
                ChannelListener listener;
                synchronized (mService.mListeners) {
                    listener = mService.mListeners.get(request.key);
                    if (listener == null) {
                        mLogger.warn("Key {} has no attached listener", request.key);
                        continue;
                    }
                }

                try {
                    listener.onRequestedUpdate(request.key, request.param);
                } catch (Throwable t) {
                    mLogger.error("Error while handling (for custom update) key {}", request.key, t);
                }
            }
        }

        private void processOpenRequests() {
            mOpenRequestsLocal.clear();
            synchronized (mService.mOpenRequests) {
                if (mService.mOpenRequests.isEmpty()) {
                    return;
                }

                mOpenRequestsLocal.addAll(mService.mOpenRequests);
                mService.mOpenRequests.clear();
            }

            for (OpenRequest<?> request : mOpenRequestsLocal) {
                BaseChannel channel = null;
                try {
                    channel = request.opener.open();
                    UpdateRegistration registration = channel.register(mService, request.listenerToRegister);

                    request.onOpen(channel, registration);
                } catch (Throwable t) {
                    mLogger.error("Error while opening new channel", t);

                    if (channel != null) {
                        Closeables.silentClose(channel);
                    }

                    try {
                        request.listener.onError(t);
                    } catch (Throwable ignore) {}
                }
            }
        }
    }

    private static class UpdateRegistrationImpl implements UpdateRegistration {

        private final SelectionKey mKey;
        private final Queue<UpdateRequest> mCustomUpdateRequests;

        UpdateRegistrationImpl(SelectionKey key, Queue<UpdateRequest> customUpdateRequests) {
            mKey = key;
            mCustomUpdateRequests = customUpdateRequests;
        }

        @Override
        public SelectionKey getKey() {
            return mKey;
        }

        @Override
        public void requestConnectionUpdates() {
            mKey.interestOps(SelectionKey.OP_CONNECT);
        }

        @Override
        public void requestReadUpdates() {
            mKey.interestOps(SelectionKey.OP_READ);
        }

        @Override
        public void requestReadWriteUpdates() {
            mKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

        @Override
        public void requestUpdate(Object param) {
            synchronized (mCustomUpdateRequests) {
                mCustomUpdateRequests.add(new UpdateRequest(mKey, param));
            }
        }

        @Override
        public void cancel() {
            mKey.cancel();
        }
    }

    private static class UpdateRequest {
        public final SelectionKey key;
        public final Object param;

        private UpdateRequest(SelectionKey key, Object param) {
            this.key = key;
            this.param = param;
        }
    }

    private static class OpenRequest<T extends BaseChannel> {
        public final NetChannelOpener<? extends T> opener;
        public final ChannelOpenListener<? super T> listener;
        public final ChannelListener listenerToRegister;

        private OpenRequest(NetChannelOpener<? extends T> opener,
                            ChannelOpenListener<? super T> listener,
                            ChannelListener listenerToRegister) {
            this.opener = opener;
            this.listener = listener;
            this.listenerToRegister = listenerToRegister;
        }

        void onOpen(BaseChannel channel, UpdateRegistration registration) {
            //noinspection unchecked
            listener.onOpen((T) channel, registration);
        }
    }
}
