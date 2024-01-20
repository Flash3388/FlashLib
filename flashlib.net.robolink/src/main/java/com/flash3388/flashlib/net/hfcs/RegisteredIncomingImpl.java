package com.flash3388.flashlib.net.hfcs;

import com.beans.observables.RegisteredListener;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.notifier.EventController;

public class RegisteredIncomingImpl<T> implements HfcsRegisteredIncoming<T> {

    private final EventController mEventController;
    private final HfcsInType<T> mType;

    public RegisteredIncomingImpl(EventController eventController, HfcsInType<T> type) {
        mEventController = eventController;
        mType = type;
    }

    @Override
    public RegisteredListener addListener(HfcsInListener<T> listener) {
        mEventController.registerListener(listener, (event)-> {
            //noinspection rawtypes
            return event instanceof DataReceivedEvent &&
                    ((DataReceivedEvent)event).getType().getKey() == mType.getKey();
        });

        return new RegisteredListenerImpl(mEventController, listener);
    }
}
