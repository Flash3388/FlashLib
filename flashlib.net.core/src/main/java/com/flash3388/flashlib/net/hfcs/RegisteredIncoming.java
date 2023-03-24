package com.flash3388.flashlib.net.hfcs;

/**
 * Control and management of incoming HFCS data.
 *
 * @param <T> type of data
 * @since FlashLib 3.2.0
 */
public interface RegisteredIncoming<T> {

    void addListener(DataListener<T> listener);
}
