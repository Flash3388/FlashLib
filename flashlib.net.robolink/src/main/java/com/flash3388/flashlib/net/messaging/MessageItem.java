package com.flash3388.flashlib.net.messaging;

import com.flash3388.flashlib.util.unique.InstanceId;

public class MessageItem {

    private final InstanceId mTarget;
    private final Message mMessage;

    public MessageItem(InstanceId target, Message message) {
        mTarget = target;
        mMessage = message;
    }

    public InstanceId getTarget() {
        return mTarget;
    }

    public Message getMessage() {
        return mMessage;
    }
}
