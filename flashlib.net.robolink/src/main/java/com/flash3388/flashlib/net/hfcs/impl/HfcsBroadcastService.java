package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.hfcs.messages.InPackage;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.net.message.BroadcastUdpMessagingChannel;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

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
        MessageReader messageReader = new MessageReaderImpl(messageTypes);

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

    }
}
