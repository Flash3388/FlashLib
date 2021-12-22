package com.flash3388.flashlib.scheduling.threading;

import java.util.function.Supplier;

public interface MtWorkers {

    void runWorkers(Supplier<Runnable> taskSupplier);
    void stopWorkers();
}
