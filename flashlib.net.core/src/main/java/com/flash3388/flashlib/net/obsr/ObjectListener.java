package com.flash3388.flashlib.net.obsr;

import com.notifier.Listener;

public interface ObjectListener extends Listener {

    void onEntryModification(EntryModificationEvent event);
}
