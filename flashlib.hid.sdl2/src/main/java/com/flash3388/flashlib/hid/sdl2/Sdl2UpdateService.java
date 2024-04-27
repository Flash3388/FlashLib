package com.flash3388.flashlib.hid.sdl2;

import com.castle.concurrent.service.SingleUseService;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;

import java.util.concurrent.atomic.AtomicReference;

public class Sdl2UpdateService extends SingleUseService {

    private final Sdl2HidData mHidData;
    private final NamedThreadFactory mThreadFactory;
    private final AtomicReference<Thread> mThread;

    public Sdl2UpdateService(Sdl2HidData hidData, NamedThreadFactory threadFactory) {
        mHidData = hidData;
        mThreadFactory = threadFactory;
        mThread = new AtomicReference<>();
    }

    @Override
    protected void startRunning() {
        Thread thread = mThreadFactory.newThread("hid-update", new Sdl2UpdateTask(mHidData));
        mThread.set(thread);
        thread.start();
    }

    @Override
    protected void stopRunning() {
        Thread thread = mThread.get();
        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
