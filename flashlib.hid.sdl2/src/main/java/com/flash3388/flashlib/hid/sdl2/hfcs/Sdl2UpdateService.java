package com.flash3388.flashlib.hid.sdl2.hfcs;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.hid.data.HidData;

public class Sdl2UpdateService extends SingleUseService {

    private final HidData mHidData;
    private Thread mUpdateThread;

    public Sdl2UpdateService(HidData hidData) {
        mHidData = hidData;
        mUpdateThread = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mUpdateThread = new Thread(
                new Sdl2UpdateTask(mHidData),
                "Sdl2UpdateService-Hfcs-UpdateTask");
        mUpdateThread.setDaemon(true);
        mUpdateThread.start();
    }

    @Override
    protected void stopRunning() {
        mUpdateThread.interrupt();
        mUpdateThread = null;
    }
}
