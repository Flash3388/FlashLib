package com.flash3388.flashlib.net.robolink.impl;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.robolink.DataListener;
import com.flash3388.flashlib.net.robolink.InboundPacketsReceiver;
import com.flash3388.flashlib.net.robolink.Packet;
import com.flash3388.flashlib.net.robolink.PacketsSender;
import com.flash3388.flashlib.net.robolink.Remote;
import com.flash3388.flashlib.net.robolink.RemotesStorage;
import com.flash3388.flashlib.net.robolink.io.RoboLinkChannel;
import com.flash3388.flashlib.time.Clock;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RoboLinkService extends TerminalServiceBase
        implements InboundPacketsReceiver, PacketsSender, RemotesStorage {

    private final RoboLinkChannel mChannel;
    private final EventController mEventController;
    private final Clock mClock;
    private final Logger mLogger;
    private final BlockingQueue<SendRequest> mRequestsQueue;

    private Thread mUpdatesThread;
    private Thread mWriteThread;

    public RoboLinkService(String id, EventController eventController, Clock clock, Logger logger) {
        mChannel = new RoboLinkChannel(id, clock, logger);
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
