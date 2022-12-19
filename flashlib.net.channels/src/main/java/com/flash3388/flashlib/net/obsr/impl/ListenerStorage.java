package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryValueListener;
import com.flash3388.flashlib.net.obsr.StoredEntry;
import com.flash3388.flashlib.net.obsr.ValueChangedEvent;
import com.notifier.EventController;

public class ListenerStorage {

    private final EventController mController;

    public ListenerStorage(EventController controller) {
        mController = controller;
    }

    public void addEntryValueListener(StoredEntry entry, EntryValueListener listener) {
        mController.registerListener(listener, (event)-> {
            return event instanceof ValueChangedEvent && ((ValueChangedEvent) event).getEntry().equals(entry);
        });
    }

    public void fireEntryValueChanged(ValueChangedEvent event) {
        mController.fire(event, ValueChangedEvent.class, EntryValueListener.class, EntryValueListener::onValueChanged);
    }
}
