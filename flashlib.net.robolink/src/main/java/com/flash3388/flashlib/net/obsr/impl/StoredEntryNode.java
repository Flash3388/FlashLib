package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryValueObservableProperty;
import com.flash3388.flashlib.net.obsr.StorageBasedEntry;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueProperty;
import com.flash3388.flashlib.time.Time;

public class StoredEntryNode {

    private Value mValue;
    private Time mLastChangeTimestamp;

    private final StorageBasedEntry mEntry;
    private final EntryValueObservableProperty mValueProperty;

    public StoredEntryNode(StorageBasedEntry entry, EntryValueObservableProperty valueProperty) {
        mEntry = entry;
        mValueProperty = valueProperty;
        mValue = new Value();
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
