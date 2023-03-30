package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.channels.messsaging.MessageAndType;
import com.flash3388.flashlib.net.channels.messsaging.MessageInfo;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.messages.HfcsInMessage;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.net.messaging.InMessage;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;

public class BasicChannelUpdateHandler implements MessagingChannel.UpdateHandler {

    private final EventController mEventController;
    private final Logger mLogger;

    public BasicChannelUpdateHandler(EventController eventController, Logger logger) {
        mEventController = eventController;
        mLogger = logger;
    }

    @Override
    public void onNewMessage(MessageInfo messageInfo, InMessage message) {
        assert messageInfo.getType().getKey() == HfcsMessageType.KEY;
        assert message instanceof HfcsInMessage;

        HfcsInMessage dataMessage = ((HfcsInMessage) message);
        InType<?> inType = dataMessage.getType();
        Object inData = dataMessage.getData();

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

    @Override
    public Optional<List<MessageAndType>> getMessageForNewClient() {
        return Optional.empty();
    }
}
