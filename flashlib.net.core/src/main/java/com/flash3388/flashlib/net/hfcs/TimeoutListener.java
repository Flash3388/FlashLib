package com.flash3388.flashlib.net.hfcs;

import com.notifier.Listener;

/**
 * Listener for timeouts of incoming data in HFCS.
 *
 * @param <T> type of data packets.
 * @since FlashLib 3.2.0
 */
public interface TimeoutListener<T> extends Listener {

    /**
     * Invoked when the data type has reached its timeout.
     *
     * @param event event describing the timeout.
     */
    void onTimeout(TimeoutEvent<T> event);
}
