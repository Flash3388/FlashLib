package com.flash3388.flashlib.net.obsr;

import com.notifier.Listener;

public interface EntryListener extends Listener {

    void onEntryModification(EntryModificationEvent event);
}
