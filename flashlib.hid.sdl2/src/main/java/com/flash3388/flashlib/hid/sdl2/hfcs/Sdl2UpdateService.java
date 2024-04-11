package com.flash3388.flashlib.hid.sdl2.hfcs;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;

public class Sdl2UpdateService extends SingleUseService {

    private final HidData mHidData;
    private final NamedThreadFactory mThreadFactory;
    private Thread mUpdateThread;

    public Sdl2UpdateService(HidData hidData, NamedThreadFactory threadFactory) {
        mHidData = hidData;
        mThreadFactory = threadFactory;
        mUpdateThread = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mUpdateThread = mThreadFactory.newThread(
                "Sdl2UpdateService-Hfcs-UpdateTask",
                new Sdl2UpdateTask(mHidData));
        mUpdateThread.start();
    }

    @Override
    protected void stopRunning() {
        mUpdateThread.interrupt();
        mUpdateThread = null;
    }
}
