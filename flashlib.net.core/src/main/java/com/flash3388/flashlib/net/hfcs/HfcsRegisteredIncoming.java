package com.flash3388.flashlib.net.hfcs;

import com.notifier.RegisteredListener;

/**
 * Control and management of incoming HFCS data.
 *
 * @param <T> type of data
 * @since FlashLib 3.2.0
 */
public interface HfcsRegisteredIncoming<T> {

    RegisteredListener addListener(HfcsInListener<T> listener);
}
