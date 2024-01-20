package com.flash3388.flashlib.net.messaging;

import com.notifier.Event;

import java.util.Set;
import java.util.function.Predicate;

class MessageTypeListenerPredicate implements Predicate<Event> {

    private final Set<? extends MessageType> mTypes;

    MessageTypeListenerPredicate(Set<? extends MessageType> types) {
        mTypes = types;
    }

    @Override
    public boolean test(Event event) {
        if (!(event instanceof NewMessageEvent)) {
            return false;
        }

        NewMessageEvent newMessageEvent = (NewMessageEvent) event;
        MessageType type = newMessageEvent.getMessage().getType();
        return mTypes.contains(type);
    }
}
