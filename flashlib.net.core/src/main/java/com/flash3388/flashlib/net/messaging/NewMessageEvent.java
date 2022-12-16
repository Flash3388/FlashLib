package com.flash3388.flashlib.net.messaging;

import com.notifier.Event;

public class NewMessageEvent implements Event {

    private final Message mMessage;

    public NewMessageEvent(Message message) {
        mMessage = message;
    }

    public Message getMessage() {
        return mMessage;
    }
}
