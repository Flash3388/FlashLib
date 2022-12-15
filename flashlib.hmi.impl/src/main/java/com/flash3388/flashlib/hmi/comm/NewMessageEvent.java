package com.flash3388.flashlib.hmi.comm;

import com.notifier.Event;

public class NewMessageEvent implements Event {

    private final BasicMessage mBasicMessage;

    public NewMessageEvent(BasicMessage basicMessage) {
        mBasicMessage = basicMessage;
    }

    public BasicMessage getMessage() {
        return mBasicMessage;
    }
}
