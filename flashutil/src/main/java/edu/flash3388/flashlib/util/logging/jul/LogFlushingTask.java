package edu.flash3388.flashlib.util.logging.jul;

import edu.flash3388.flashlib.time.Time;

import java.util.logging.Handler;

public class LogFlushingTask implements Runnable {

    private static final Time DEFAULT_FLUSHING_PERIOD = Time.milliseconds(100);

    private final Handler mHandler;

    public LogFlushingTask(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                mHandler.flush();

                Thread.sleep(DEFAULT_FLUSHING_PERIOD.getAsMillis());
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
