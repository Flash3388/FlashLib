package com.flash3388.flashlib.net.messaging;

import com.notifier.Event;

public class NewMessageEvent implements Event {

    private final MessageMetadata mMetadata;
    private final Message mMessage;

    public NewMessageEvent(MessageMetadata metadata, Message message) {
        mMetadata = metadata;
        mMessage = message;
    }

    public MessageMetadata getMetadata() {
        return mMetadata;
    }

    public MessageType getType() {
        return mMetadata.getType();
    }

    public Message getMessage() {
        return mMessage;
    }

    public <T extends Message> T getMessage(Class<T> type) {
        if (type.isInstance(mMessage)) {
            return type.cast(mMessage);
        }

        throw new ClassCastException(String.format("Message is not of type %s", type.getName()));
    }
}
