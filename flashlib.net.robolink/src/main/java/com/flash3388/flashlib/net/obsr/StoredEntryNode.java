package com.flash3388.flashlib.net.obsr;

import com.flash3388.flashlib.time.Time;

class StoredEntryNode {

    private Value mValue;
    private Time mLastChangeTimestamp;

    private final StorageBasedEntry mEntry;
    private final EntryValueObservableProperty mValueProperty;

    public StoredEntryNode(StorageBasedEntry entry, EntryValueObservableProperty valueProperty) {
        mEntry = entry;
        mValueProperty = valueProperty;
        mValue = Value.empty();
        mLastChangeTimestamp = Time.INVALID;
    }

    public StorageBasedEntry getEntry() {
        return mEntry;
    }

    public ValueProperty getValueProperty() {
        return mValueProperty;
    }

    public Value getValue() {
        return mValue;
    }

    public Time getLastChangeTimestamp() {
        return mLastChangeTimestamp;
    }

    public void setValue(Value value, Time currentTime) {
        Value oldValue = mValue;

        mValue = value;
        mLastChangeTimestamp = currentTime;

        mValueProperty.invokeChangeListener(oldValue, mValue);
    }
}
