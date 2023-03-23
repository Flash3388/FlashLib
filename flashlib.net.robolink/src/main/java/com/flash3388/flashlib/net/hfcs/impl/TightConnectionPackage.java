package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.messages.DataMessage;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.message.ConfigurableTargetUdpMessagingChannel;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.Queue;

public class TightConnectionPackage implements MessagingChannel.UpdateHandler {

    private final EventController mEventController;
    private final Clock mClock;
    private final Logger mLogger;
    private final Queue<SocketAddress> mPotentialRemotes;
    private final ConfigurableTargetUdpMessagingChannel mChannel;
    private final Time mReceiveTimerExpiration;

    private SocketAddress mCurrentUseAddress;
    private Time mLastReceivedTimestamp;

    public TightConnectionPackage(EventController eventController,
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
        return now.sub(mLastReceivedTimestamp).largerThanOrEquals(mReceiveTimerExpiration);
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
