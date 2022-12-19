package com.flash3388.flashlib.net.obsr;

import com.notifier.Event;

public class ValueChangedEvent implements Event {

    private final StoredEntry mEntry;
    private final Value mOldValue;
    private final Value mNewValue;

    public ValueChangedEvent(StoredEntry entry, Value oldValue, Value newValue) {
        mEntry = entry;
        mOldValue = oldValue;
        mNewValue = newValue;
    }

    public StoredEntry getEntry() {
        return mEntry;
    }

    public Value getOldValue() {
        return mOldValue;
    }

    public Value getNewValue() {
        return mNewValue;
    }
}
