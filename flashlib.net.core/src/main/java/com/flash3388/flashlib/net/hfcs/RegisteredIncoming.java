package com.flash3388.flashlib.net.hfcs;

import com.beans.observables.RegisteredListener;

/**
 * Control and management of incoming HFCS data.
 *
 * @param <T> type of data
 * @since FlashLib 3.2.0
 */
public interface RegisteredIncoming<T> {

    RegisteredListener addListener(DataListener<T> listener);
    RegisteredListener addTimeoutListener(TimeoutListener<T> listener);
}
