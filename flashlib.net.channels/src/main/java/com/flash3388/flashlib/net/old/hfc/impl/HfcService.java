package com.flash3388.flashlib.net.old.hfc.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.hfc.DataListener;
import com.flash3388.flashlib.net.hfc.InboundPacketsReceiver;
import com.flash3388.flashlib.net.hfc.Packet;
import com.flash3388.flashlib.net.hfc.PacketsSender;
import com.flash3388.flashlib.net.old.hfc.io.HfcChannel;
import com.flash3388.flashlib.net.Remote;
import com.flash3388.flashlib.net.RemotesStorage;
import com.flash3388.flashlib.time.Clock;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class HfcService extends TerminalServiceBase
        implements InboundPacketsReceiver, PacketsSender, RemotesStorage {
    // TODO: RoboLink should be based on this packets thing, but is actually sending periodically specific data
    // and receiving specific data

    private final HfcChannel mChannel;
    private final EventController mEventController;
    private final Clock mClock;
    private final Logger mLogger;
    private final BlockingQueue<SendRequest> mRequestsQueue;

    private Thread mUpdatesThread;
    private Thread mWriteThread;

    public HfcService(String id, EventController eventController, Clock clock, Logger logger) {
        mChannel = new HfcChannel(id, clock, logger, packetTypes);
        mEventController = eventController;
        mClock = clock;
        mLogger = logger;
        mRequestsQueue = new LinkedBlockingQueue<>();

        mUpdatesThread = null;
        mWriteThread = null;
    }

    @Override
    public void addDataListener(DataListener listener) {
        mEventController.registerListener(listener);
    }

    @Override
    public void send(String remoteId, Packet packet) {
        mRequestsQueue.add(new SendRequest(packet, remoteId));
    }

    @Override
    public void broadcast(Packet packet) {
        mRequestsQueue.add(new SendRequest(packet, null));
    }

    @Override
    public Optional<Remote> getById(String id) {
        return mChannel.getRemotesStorage().getById(id);
    }

    @Override
    protected void startRunning() throws ServiceException {
        mWriteThread = new Thread(
                new WriteTask(mChannel, mRequestsQueue, mClock, mLogger),
                "RoboLinkService-WriteTask");
        mWriteThread.setDaemon(true);

        mUpdatesThread = new Thread(
                new UpdatesTask(mChannel, mEventController, mLogger),
                "RoboLinkService-UpdatesThread");
        mUpdatesThread.setDaemon(true);

        mWriteThread.start();
        mUpdatesThread.start();
    }

    @Override
    protected void stopRunning() {
        mWriteThread.interrupt();
        mWriteThread = null;

        mUpdatesThread.interrupt();
        mUpdatesThread = null;

        Closeables.silentClose(mChannel);
    }
}
