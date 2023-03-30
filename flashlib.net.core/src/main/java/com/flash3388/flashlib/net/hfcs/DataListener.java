package com.flash3388.flashlib.net.hfcs;

import com.notifier.Listener;

/**
 * Listener for data received by packets of HFCS.
 *
 * @param <T> type of data being received.
 * @since FlashLib 3.2.0
 */
public interface DataListener<T> extends Listener {

    /**
     * Invoked when new data is received.
     *
     * @param event event describing the data received.
     */
    void onReceived(DataReceivedEvent<T> event);
}
