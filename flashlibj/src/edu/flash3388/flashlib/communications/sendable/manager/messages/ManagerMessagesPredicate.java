package edu.flash3388.flashlib.communications.sendable.manager.messages;

import edu.flash3388.flashlib.communications.message.event.MessageEvent;
import edu.flash3388.flashlib.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ManagerMessagesPredicate implements Predicate<Event> {

    private final Set<Integer> mHeaders;

    public ManagerMessagesPredicate() {
        mHeaders = new HashSet<Integer>();
        mHeaders.add(DiscoveryRequestMessage.HEADER);
        mHeaders.add(DiscoveryDataMessage.HEADER);
        mHeaders.add(PairRequestMessage.HEADER);
        mHeaders.add(PairSuccessMessage.HEADER);
        mHeaders.add(PairFailureMessage.HEADER);
        mHeaders.add(SessionCloseMessage.HEADER);
        mHeaders.add(SendableMessage.HEADER);
    }

    @Override
    public boolean test(Event event) {
        return event instanceof MessageEvent && mHeaders.contains(((MessageEvent)event).getMessage().getHeader());
    }
}
