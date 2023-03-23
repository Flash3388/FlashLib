package com.flash3388.flashlib.net.hfcs;

public interface RegisteredIncoming<T> {

    void addListener(DataListener<T> listener);
}
