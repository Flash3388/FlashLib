package edu.flash3388.flashlib.util.collections;

import edu.flash3388.flashlib.util.concurrent.Interrupts;

import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

public class BlockingQueueConsumer<T> implements Consumer<T> {

    private final BlockingQueue<T> mBlockingQueue;

    public BlockingQueueConsumer(BlockingQueue<T> blockingQueue) {
        mBlockingQueue = blockingQueue;
    }

    @Override
    public void accept(T t) {
        try {
            mBlockingQueue.put(t);
        } catch (InterruptedException e) {
            Interrupts.preserveInterruptState();
        }
    }
}
