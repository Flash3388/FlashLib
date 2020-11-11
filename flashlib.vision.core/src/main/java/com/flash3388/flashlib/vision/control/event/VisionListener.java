package com.flash3388.flashlib.vision.control.event;

import com.notifier.Listener;

public interface VisionListener extends Listener {

    void onNewResult(NewResultEvent e);
}
