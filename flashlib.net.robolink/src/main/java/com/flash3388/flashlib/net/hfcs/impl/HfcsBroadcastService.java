package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
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
import com.flash3388.flashlib.net.message.BroadcastUdpMessagingChannel;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.net.message.WritableMessagingChannel;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.function.Supplier;

public class HfcsBroadcastService extends SingleUseService implements HfcsRegistry {

    private static final int BIND_PORT = 5005;

    private final Clock mClock;
    private final Logger mLogger;

    private final KnownInDataTypes mInDataTypes;
    private final EventController mEventController;
    private final MessagingChannel mChannel;
    private final BlockingQueue<OutDataNode> mOutDataQueue;

    private Thread mUpdateThread;
    private Thread mWriteThread;

    public HfcsBroadcastService(InstanceId ourId, Clock clock, Logger logger) {
        mClock = clock;
        mLogger = logger;

        mInDataTypes = new KnownInDataTypes();

        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(new DataMessageType(new InPackage(mInDataTypes)));

        MessageWriter messageWriter = new MessageWriterImpl(ourId);
        MessageReader messageReader = new MessageReaderImpl(ourId, messageTypes);

        mEventController = Controllers.newSyncExecutionController();
        mChannel = new BroadcastUdpMessagingChannel(
                BIND_PORT,
                ourId,
                messageWriter,
                messageReader,
                clock,
                logger
        );

        mOutDataQueue = new DelayQueue<>();

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
                new UpdateTask(mChannel, mEventController, mLogger),
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
        mUpdateThread.interrupt();
        mUpdateThread = null;

        mWriteThread.interrupt();
        mWriteThread = null;

        Closeables.silentClose(mChannel);
    }

    private static class WriteTask implements Runnable {

        private final BlockingQueue<OutDataNode> mDataQueue;
        private final WritableMessagingChannel mChannel;
        private final Logger mLogger;

        public WriteTask(BlockingQueue<OutDataNode> dataQueue, WritableMessagingChannel channel, Logger logger) {
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

    private static class UpdateTask implements Runnable {

        private final MessagingChannel mChannel;
        private final Logger mLogger;
        private final MessagingChannel.UpdateHandler mHandler;

        public UpdateTask(MessagingChannel channel, EventController eventController, Logger logger) {
            mChannel = channel;
            mLogger = logger;
            mHandler = new ChannelUpdateHandler(eventController, logger);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mChannel.handleUpdates(mHandler);
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

    private static class ChannelUpdateHandler implements MessagingChannel.UpdateHandler {

        private final EventController mEventController;
        private final Logger mLogger;

        public ChannelUpdateHandler(EventController eventController, Logger logger) {
            mEventController = eventController;
            mLogger = logger;
        }

        @Override
        public void onNewMessage(MessageInfo messageInfo, Message message) {
            assert messageInfo.getType().getKey() == DataMessageType.KEY;
            assert message instanceof DataMessage;

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
