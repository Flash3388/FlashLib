package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.net.channels.messsaging.MessageHeader;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;

class ClientReadTask implements Runnable {

    private final MessagingChannel mChannel;
    private final Logger mLogger;
    private final MessagingChannel.UpdateHandler mHandler;

    private final PingContext mPingContext;


    ClientReadTask(Messenger service,
                   MessagingChannel channel,
                   EventController eventController,
                   ServerClock clock,
                   Logger logger) {
        mChannel = channel;
        mLogger = logger;
        mPingContext = new PingContext(service, channel, clock, logger);
        mHandler = new UpdateHandlerImpl(eventController, mPingContext, logger);
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            mPingContext.pingIfNecessary();

            try {
                mChannel.processUpdates(mHandler);
            } catch (Throwable t) {
                mLogger.error("ClientReadTask: Error processing updates", t);
            }
        }
    }

    private static class UpdateHandlerImpl implements MessagingChannel.UpdateHandler {

        private final EventController mEventController;
        private final PingContext mPingContext;
        private final Logger mLogger;

        private UpdateHandlerImpl(EventController eventController,
                                  PingContext pingContext,
                                  Logger logger) {
            mEventController = eventController;
            mPingContext = pingContext;
            mLogger = logger;
        }

        @Override
        public void onConnect() {
            mPingContext.onConnect();
        }

        @Override
        public void onDisconnect() {
            mPingContext.onDisconnect();
        }

        @Override
        public void onNewMessage(MessageHeader header, Message message) {
            MessageMetadata metadata = new MessageMetadataImpl(
                    header.getSender(),
                    header.getSendTime(),
                    message.getType());

            if (message.getType().equals(PingMessage.TYPE)) {
                PingMessage pingMessage = (PingMessage) message;
                mPingContext.onPingResponse(header, pingMessage);
            } else {
                mLogger.debug("ClientReadTask: received message, alerting listeners");

                mEventController.fire(
                        new NewMessageEvent(metadata, message),
                        NewMessageEvent.class,
                        MessageListener.class,
                        MessageListener::onNewMessage
                );
            }
        }
    }

    private static class PingContext {

        private static final Time PING_INTERVAL = Time.milliseconds(500);
        private static final int MAX_UNRESPONDED_PINGS = 3;

        private final WeakReference<Messenger> mMessenger;
        private final MessagingChannel mChannel;
        private final ServerClock mClock;
        private final Logger mLogger;

        private boolean mShouldPing;
        private Time mLastPingTime;
        private boolean mPingSent;
        private boolean mLastPingResponded;
        private int mUnrespondedPingCount;

        private PingContext(Messenger messenger, MessagingChannel channel, ServerClock clock, Logger logger) {
            mMessenger = new WeakReference<>(messenger);
            mChannel = channel;
            mClock = clock;
            mLogger = logger;

            mShouldPing = false;
            mLastPingTime = Time.INVALID;
            mUnrespondedPingCount = 0;
            mPingSent = false;
            mLastPingResponded = false;
        }

        public void onConnect() {
            mShouldPing = true;
            mLastPingTime = Time.INVALID;
            mUnrespondedPingCount = 0;
            mPingSent = false;
            mLastPingResponded = false;

            sendPing();
        }

        public void onDisconnect() {
            mShouldPing = false;
        }

        public void pingIfNecessary() {
            if (!mShouldPing) {
                return;
            }

            Time now = mClock.getBaseClock().currentTime();
            if (!mLastPingTime.isValid() || now.sub(mLastPingTime).after(PING_INTERVAL)) {
                if (mPingSent && !mLastPingResponded) {
                    mLogger.warn("ClientReadTask: Last ping did not receive response");
                    mUnrespondedPingCount++;
                }
                if (mUnrespondedPingCount >= MAX_UNRESPONDED_PINGS) {
                    mLogger.warn("ClientReadTask: Too many pings did not receive response, resetting");
                    mChannel.resetConnection();
                    mShouldPing = false;
                    return;
                }

                sendPing();
            }
        }

        public void sendPing() {
            Messenger messenger = mMessenger.get();
            if (messenger == null) {
                throw new IllegalStateException("service garbage collected");
            }

            mLogger.debug("ClientReadTask: Sending ping message");

            Time now = mClock.getBaseClock().currentTime();
            mLastPingTime = now;
            mPingSent = true;
            mLastPingResponded = false;

            messenger.send(new PingMessage(now));
        }

        public void onPingResponse(MessageHeader header, PingMessage message) {
            mLogger.debug("ClientReadTask: Received ping response");
            mLastPingResponded = true;
            mPingSent = false;
            mUnrespondedPingCount = 0;

            mClock.readjustOffset(header.getSendTime(), message.getTime());
        }
    }
}
