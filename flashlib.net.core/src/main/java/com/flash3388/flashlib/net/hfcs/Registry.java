package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.time.Time;

import java.util.function.Supplier;

public interface Registry {

    void registerOutgoing(Type type,
                          Time period,
                          Supplier<? extends OutData> supplier);

    <T> RegisteredIncoming<T> registerIncoming(InType<T> type);
}
