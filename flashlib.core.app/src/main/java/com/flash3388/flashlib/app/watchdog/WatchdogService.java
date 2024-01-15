package com.flash3388.flashlib.app.watchdog;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.util.concurrent.Sleeper;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class WatchdogService extends TerminalServiceBase {

    private static final Logger LOGGER = Logging.getLogger("Watchdog");

    private final PriorityBlockingQueue<InternalWatchdog> mWatchdogs;
    private Thread mThread;

    public WatchdogService() {
        mWatchdogs = new PriorityBlockingQueue<>(3,
                Comparator.comparing(InternalWatchdog::getTimeout));
    }

    public void register(InternalWatchdog watchdog) {
        mWatchdogs.add(watchdog);
    }

    @Override
    protected void startRunning() throws ServiceException {
        mThread = new Thread(new Task(mWatchdogs), "WatchdogThread");
        mThread.start();
    }

    @Override
    protected void stopRunning() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
    }

    private static class Task implements Runnable {

        private final BlockingQueue<InternalWatchdog> mWatchdogs;
        private final Sleeper mSleeper;

        private Task(BlockingQueue<InternalWatchdog> watchdogs) {
            mWatchdogs = watchdogs;
            mSleeper = new Sleeper();
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    InternalWatchdog watchdog = mWatchdogs.take();
                    try {
                        if (watchdog.isExpired() || !watchdog.isEnabled()) {
                            continue;
                        }

                        // TODO: BETTER WAIT FOR TIMEOUT
                        mSleeper.sleep(watchdog.getTimeLeftToTimeout());
                        watchdog.checkFed();
                    } finally {
                        mWatchdogs.add(watchdog);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Throwable t) {
                    LOGGER.error("Error while checking watchdog", t);
                }
            }
        }
    }
}
