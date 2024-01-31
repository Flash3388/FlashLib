package com.flash3388.flashlib.net.obsr;

import com.notifier.Event;

import java.util.function.Predicate;

public class PathListenerPredicate implements Predicate<Event> {

    private final String mPath;

    public PathListenerPredicate(String path) {
        mPath = path;
    }

    @Override
    public boolean test(Event event) {
        return event instanceof EntryModificationEvent &&
                ((EntryModificationEvent)event).getPath().startsWith(mPath);
    }
}
