package com.flash3388.flashlib.net.hfcs;

import com.notifier.Listener;

/**
 * Listener for incoming data in HFCS.
 *
 * @param <T> type of data being received.
 * @since FlashLib 3.2.0
 */
public interface HfcsInListener<T> extends Listener {

    /**
     * Invoked when new data is received.
     *
     * @param event event describing the data received.
     */
    void onReceived(DataReceivedEvent<T> event);

    /**
     * Invoked when the data type has reached its timeout.
     *
     * @param event event describing the timeout.
     */
    void onTimeout(TimeoutEvent<T> event);
}
