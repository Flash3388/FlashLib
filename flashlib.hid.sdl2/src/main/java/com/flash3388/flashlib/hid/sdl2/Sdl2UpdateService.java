package com.flash3388.flashlib.hid.sdl2;

import com.castle.concurrent.service.SingleUseService;

import java.util.concurrent.atomic.AtomicReference;

public class Sdl2UpdateService extends SingleUseService {

    private final Sdl2HidData mHidData;
    private final AtomicReference<Thread> mThread;

    public Sdl2UpdateService(Sdl2HidData hidData) {
        mHidData = hidData;
        mThread = new AtomicReference<>();
    }

    @Override
    protected void startRunning() {
        Thread thread = new Thread(new Sdl2UpdateTask(mHidData), "hid-update");
        thread.setDaemon(true);
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
