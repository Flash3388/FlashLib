package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.messages.DataMessage;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.message.Message;
import com.flash3388.flashlib.net.message.MessageInfo;
import com.flash3388.flashlib.net.message.MessagingChannel;
import com.notifier.EventController;
import org.slf4j.Logger;

public class BasicChannelUpdateHandler implements MessagingChannel.UpdateHandler {

    private final EventController mEventController;
    private final Logger mLogger;

    public BasicChannelUpdateHandler(EventController eventController, Logger logger) {
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
