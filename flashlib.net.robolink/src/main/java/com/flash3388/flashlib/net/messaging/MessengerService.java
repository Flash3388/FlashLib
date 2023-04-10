package com.flash3388.flashlib.net.messaging;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.tcp.TcpClientConnector;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessengerService extends SingleUseService implements Messenger {

    private static final Logger LOGGER = Logging.getLogger("Comm", "MessengerService");

    private final MessagingChannel mChannel;
    private final Clock mClock;
    private final EventController mEventController;
    private final BlockingQueue<PendingWriteMessage> mWriteQueue;

    private Thread mReadThread;
    private Thread mWriteThread;

    public MessengerService(InstanceId instanceId, Clock clock,
                            KnownMessageTypes messageTypes,
                            SocketAddress serverAddress) {
        mClock = clock;
        mEventController = Controllers.newSyncExecutionController();
        mWriteQueue = new LinkedBlockingQueue<>();

        mChannel = new BasicMessagingChannel(new TcpClientConnector(LOGGER),
                serverAddress, instanceId, LOGGER, messageTypes);

        mReadThread = null;
        mWriteThread = null;
    }

    @Override
    public RegisteredListener addListener(MessageListener listener) {
        mEventController.registerListener(listener);
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public RegisteredListener addListener(MessageListener listener, MessageType type) {
        mEventController.registerListener(listener, (event) -> {
            return event instanceof NewMessageEvent &&
                    ((NewMessageEvent) event).getMetadata().getType().equals(type);
        });
        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public void send(Message message) {
        LOGGER.debug("Queueing message of type {}", message.getType().getKey());
        mWriteQueue.add(new PendingWriteMessage(message.getType(), message));
    }

    @Override
    protected void startRunning() throws ServiceException {
        mReadThread = new Thread(
                new ReadTask(mChannel, LOGGER, mEventController, mClock),
                "MessengerService-ReadTask");
        mReadThread.setDaemon(true);
        mReadThread.start();

        mWriteThread = new Thread(
                new WriteTask(mChannel, mWriteQueue, LOGGER),
                "MessengerService-WriteTask");
        mWriteThread.setDaemon(true);
        mWriteThread.start();
    }

    @Override
    protected void stopRunning() {
        mReadThread.interrupt();
        mReadThread = null;

        mWriteThread.interrupt();
        mWriteThread = null;

    }
}
