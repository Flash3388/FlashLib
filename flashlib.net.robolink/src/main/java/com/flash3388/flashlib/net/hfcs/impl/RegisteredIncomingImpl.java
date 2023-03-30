package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.notifier.EventController;

public class RegisteredIncomingImpl<T> implements RegisteredIncoming<T> {

    private final EventController mEventController;
    private final InType<T> mType;

    public RegisteredIncomingImpl(EventController eventController, InType<T> type) {
        mEventController = eventController;
        mType = type;
    }

    @Override
    public void addListener(DataListener<T> listener) {
        mEventController.registerListener(listener, (event)-> {
            //noinspection rawtypes
            return event instanceof DataReceivedEvent &&
                    ((DataReceivedEvent)event).getType().getKey() == mType.getKey();
        });
    }
}
