package com.flash3388.flashlib.net.hfcs;

import com.notifier.EventController;
import com.notifier.RegisteredListener;

class RegisteredIncomingImpl<T> implements HfcsRegisteredIncoming<T> {

    private final EventController mEventController;
    private final HfcsInType<T> mType;

    RegisteredIncomingImpl(EventController eventController, HfcsInType<T> type) {
        mEventController = eventController;
        mType = type;
    }

    @Override
    public RegisteredListener addListener(HfcsInListener<T> listener) {
        return mEventController.registerListenerForEvent(
                listener,
                BaseHfcsInEvent.class,
                (event)-> event.getType().getKey() == mType.getKey());
    }
}
