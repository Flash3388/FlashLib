package com.flash3388.flashlib.net.messaging;

import com.notifier.Event;

public class NewMessageEvent implements Event {

    private final MessageMetadata mMetadata;
    private final InMessage mMessage;

    public NewMessageEvent(MessageMetadata metadata, InMessage message) {
        mMetadata = metadata;
        mMessage = message;
    }

    public MessageMetadata getMetadata() {
        return mMetadata;
    }

    public InMessage getMessage() {
        return mMessage;
    }

    public <T extends InMessage> T getMessage(Class<T> type) {
        if (type.isInstance(mMessage)) {
            return type.cast(mMessage);
        }

        throw new ClassCastException(String.format("Message is not of type %s", type.getName()));
    }
}
