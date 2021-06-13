package com.flash3388.flashlib.scheduling.mt;

import java.util.function.Supplier;

public interface MtWorkers {

    void runWorkers(Supplier<Runnable> taskSupplier);
    void stopWorkers();
}
