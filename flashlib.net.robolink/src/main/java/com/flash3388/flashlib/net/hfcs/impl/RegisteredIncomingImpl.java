package com.flash3388.flashlib.net.hfcs.impl;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.flash3388.flashlib.net.hfcs.DataListener;
import com.flash3388.flashlib.net.hfcs.DataReceivedEvent;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.TimeoutEvent;
import com.flash3388.flashlib.net.hfcs.TimeoutListener;
import com.notifier.EventController;

public class RegisteredIncomingImpl<T> implements RegisteredIncoming<T> {

    private final EventController mEventController;
    private final InType<T> mType;

    public RegisteredIncomingImpl(EventController eventController, InType<T> type) {
        mEventController = eventController;
        mType = type;
    }

    @Override
    public RegisteredListener addListener(DataListener<T> listener) {
        mEventController.registerListener(listener, (event)-> {
            //noinspection rawtypes
            return event instanceof DataReceivedEvent &&
                    ((DataReceivedEvent)event).getType().getKey() == mType.getKey();
        });

        return new RegisteredListenerImpl(mEventController, listener);
    }

    @Override
    public RegisteredListener addTimeoutListener(TimeoutListener<T> listener) {
        mEventController.registerListener(listener, (event)-> {
            //noinspection rawtypes
            return event instanceof TimeoutEvent &&
                    ((TimeoutEvent)event).getType().getKey() == mType.getKey();
        });

        return new RegisteredListenerImpl(mEventController, listener);
    }
}
