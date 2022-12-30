package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.DataMessage;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.hfcs.messages.InPackage;
import com.flash3388.flashlib.net.hfcs.messages.OutPackage;
import com.flash3388.flashlib.net.message.ConfigurableTargetUdpMessagingChannel;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.function.Supplier;

public class HfcsTightService extends SingleUseService implements HfcsRegistry {

    private static final int BIND_PORT = 5005;
    private static final Time RECEIVE_TIMER_EXPIRATION = Time.milliseconds(500);

    private final Clock mClock;
    private final Logger mLogger;

    private final KnownInDataTypes mInDataTypes;
    private final EventController mEventController;
    private final ConfigurableTargetUdpMessagingChannel mChannel;
    private final BlockingQueue<OutDataNode> mOutDataQueue;
    private final ConnectionPackage mConnectionPackage;

    private Thread mUpdateThread;
    private Thread mWriteThread;

    public HfcsTightService(Collection<SocketAddress> possibleAddress,
                            InstanceId ourId,
                            Clock clock,
                            Logger logger) {
        mClock = clock;
        mLogger = logger;

        mInDataTypes = new KnownInDataTypes();

        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(new DataMessageType(new InPackage(mInDataTypes)));

        MessageWriter messageWriter = new MessageWriterImpl(ourId);
        MessageReader messageReader = new MessageReaderImpl(ourId, messageTypes);

        mEventController = Controllers.newSyncExecutionController();
        mChannel = new ConfigurableTargetUdpMessagingChannel(
                BIND_PORT,
                ourId,
                messageWriter,
                messageReader,
                clock,
                logger
        );

        mOutDataQueue = new DelayQueue<>();
        mConnectionPackage = new ConnectionPackage(
                mEventController,
                clock,
                logger,
                new ArrayDeque<>(possibleAddress),
                mChannel,
                RECEIVE_TIMER_EXPIRATION
        );

        mUpdateThread = null;
        mWriteThread = null;
    }

    @Override
    public void registerOutgoing(Type type, Time period, Supplier<? extends OutData> supplier) {
        mOutDataQueue.add(new OutDataNode(mClock, type, supplier, period));
    }

    @Override
    public <T> RegisteredIncoming<T> registerIncoming(InType<T> type) {
        mInDataTypes.put(type);
        return new RegisteredIncomingImpl<>(mEventController, type);
    }

    @Override
    protected void startRunning() throws ServiceException {
        mUpdateThread = new Thread(
                new UpdateTask(mConnectionPackage, mChannel, mLogger),
                "HfcsService-UpdateTask");
        mUpdateThread.setDaemon(true);

        mWriteThread = new Thread(
                new WriteTask(mOutDataQueue, mChannel, mLogger),
                "HfcsService-WriteTask");
        mWriteThread.setDaemon(true);

        mUpdateThread.start();
        mWriteThread.start();
    }

    @Override
    protected void stopRunning() {

    }

    private static class UpdateTask implements Runnable {

        private final ConnectionPackage mConnectionPackage;
        private final ConfigurableTargetUdpMessagingChannel mChannel;
        private final Logger mLogger;

        public UpdateTask(ConnectionPackage connectionPackage,
                          ConfigurableTargetUdpMessagingChannel channel,
                          Logger logger) {
            mConnectionPackage = connectionPackage;
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    if (mConnectionPackage.isCurrentRemoteTimerExpired()) {
                        mConnectionPackage.switchRemote();
                    }

                    mChannel.handleUpdates(mConnectionPackage);
                } catch (IOException e) {
                    mLogger.error("Error in updatetask", e);
                } catch (InterruptedException e) {
                    break;
                } catch (TimeoutException e) {
                    // oh, well
                }
            }
        }
    }

    private static class WriteTask implements Runnable {

        private final BlockingQueue<OutDataNode> mDataQueue;
        private final ConfigurableTargetUdpMessagingChannel mChannel;
        private final Logger mLogger;

        public WriteTask(BlockingQueue<OutDataNode> dataQueue,
                         ConfigurableTargetUdpMessagingChannel channel,
                         Logger logger) {
            mDataQueue = dataQueue;
            mChannel = channel;
            mLogger = logger;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    OutDataNode node = mDataQueue.take();
                    Type type = node.getType();
                    OutData data = node.getData();
                    node.updateSent();
                    mDataQueue.add(node);

                    mLogger.debug("Sending data of type {}", type.getKey());
                    mChannel.write(new DataMessageType(), new DataMessage(new OutPackage(type, data)));
                } catch (InterruptedException e) {
                    break;
                } catch (IOException e) {
                    mLogger.error("Error while sending data", e);
                }
            }
        }
    }

    private static class ConnectionPackage implements MessagingChannel.UpdateHandler {

        private final EventController mEventController;
        private final Clock mClock;
        private final Logger mLogger;
        private final Queue<SocketAddress> mPotentialRemotes;
        private final ConfigurableTargetUdpMessagingChannel mChannel;
        private final Time mReceiveTimerExpiration;

        private SocketAddress mCurrentUseAddress;
        private Time mLastReceivedTimestamp;

        private ConnectionPackage(EventController eventController,
                                  Clock clock,
                                  Logger logger,
                                  Queue<SocketAddress> potentialRemotes,
                                  ConfigurableTargetUdpMessagingChannel channel,
                                  Time receiveTimerExpiration) {
            mEventController = eventController;
            mClock = clock;
            mLogger = logger;
            mPotentialRemotes = potentialRemotes;
            mChannel = channel;
            mReceiveTimerExpiration = receiveTimerExpiration;

            mCurrentUseAddress = null;
            mLastReceivedTimestamp = null;
        }

        public synchronized void switchRemote() {
            SocketAddress nextRemote = mPotentialRemotes.poll();
            if (nextRemote != null) {
                SocketAddress last = mCurrentUseAddress;
                mCurrentUseAddress = nextRemote;

                if (last != null) {
                    mPotentialRemotes.add(last);
                }

                mLogger.debug("Switching to use remote {}", mCurrentUseAddress);
                mChannel.setSendTargetAddress(mCurrentUseAddress);
            } else {
                mLogger.debug("No other remote to use, continuing to use {}", mCurrentUseAddress);
                // no other remotes, then just use the same one
            }

            // reset timestamp
            mLastReceivedTimestamp = mClock.currentTime();
        }

        public synchronized boolean isCurrentRemoteTimerExpired() {
            if (mLastReceivedTimestamp == null) {
                return true;
            }

            Time now = mClock.currentTime();
            if (now.sub(mLastReceivedTimestamp).largerThanOrEquals(mReceiveTimerExpiration)) {
                return true;
            }

            return false;
        }

        private synchronized boolean updateDataReceived(SocketAddress address) {
            if (!address.equals(mCurrentUseAddress)) {
                return false;
            }

            mLastReceivedTimestamp = mClock.currentTime();
            return true;
        }

        @Override
        public void onNewMessage(MessageInfo messageInfo, Message message) {
            assert messageInfo.getType().getKey() == DataMessageType.KEY;
            assert message instanceof DataMessage;

            SocketAddress remoteAddress = mChannel.getLastReceivedSenderAddress();
            if (!updateDataReceived(remoteAddress)) {
                mLogger.warn("Message received from unexpected remote {}", remoteAddress);
                return;
            }


            DataMessage dataMessage = ((DataMessage) message);
            InType<?> inType = dataMessage.getInType();
            Object inData = dataMessage.getInData();

            assert inData.getClass().isInstance(inData);

            mLogger.debug("Received new data of type {}", inType.getKey());

            // send to listeners
            //noinspection unchecked,rawtypes
            mEventController.fire(
                    new DataReceivedEvent(messageInfo.getSender(), inType, inData),
                    DataReceivedEvent.class,
                    DataListener.class,
                    DataListener::onReceived
            );
        }
    }
}
