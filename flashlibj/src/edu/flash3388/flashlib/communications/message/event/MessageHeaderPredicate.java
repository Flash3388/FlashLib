package edu.flash3388.flashlib.communications.message.event;

import edu.flash3388.flashlib.event.Event;

import java.util.function.Predicate;

public class MessageHeaderPredicate implements Predicate<Event> {

    private int mHeader;

    public MessageHeaderPredicate(int header) {
        mHeader = header;
    }

    @Override
    public boolean test(Event event) {
        return event instanceof MessageEvent && ((MessageEvent)event).getMessage().getHeader() == mHeader;
    }
}
