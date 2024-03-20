package com.flash3388.flashlib.net.obsr;

import java.util.function.Predicate;

public class PathListenerPredicate implements Predicate<EntryModificationEvent> {

    private final String mPath;

    public PathListenerPredicate(String path) {
        mPath = path;
    }

    @Override
    public boolean test(EntryModificationEvent event) {
        return event.getPath().startsWith(mPath);
    }
}
