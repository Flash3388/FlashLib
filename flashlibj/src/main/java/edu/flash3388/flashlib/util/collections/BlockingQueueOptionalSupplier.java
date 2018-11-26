package edu.flash3388.flashlib.util.collections;

import edu.flash3388.flashlib.util.concurrent.Interrupts;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.function.Supplier;

public class BlockingQueueOptionalSupplier<T> implements Supplier<Optional<T>> {

    private final BlockingQueue<T> mBlockingQueue;

    public BlockingQueueOptionalSupplier(BlockingQueue<T> blockingQueue) {
        mBlockingQueue = blockingQueue;
    }

    @Override
    public Optional<T> get() {
        try {
            return Optional.of(mBlockingQueue.take());
        } catch (InterruptedException e) {
            Interrupts.preserveInterruptState();
            return Optional.empty();
        }
    }
}
