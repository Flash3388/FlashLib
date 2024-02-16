package com.flash3388.flashlib.app.watchdog;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.app.concurrent.DefaultFlashLibThreadFactory;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class WatchdogService extends TerminalServiceBase {

    private static final Logger LOGGER = Logging.getLogger("Watchdog");

    private final DelayQueue<Node> mWatchdogs;
    private Thread mThread;

    public WatchdogService() {
        mWatchdogs = new DelayQueue<>();
    }

    public void register(InternalWatchdog watchdog) {
        Node node = new Node(watchdog);
        node.reset();
        mWatchdogs.add(node);
    }

    @Override
    protected void startRunning() throws ServiceException {
        mThread = DefaultFlashLibThreadFactory.newThread("WatchdogThread", new Task(mWatchdogs));
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

        private final DelayQueue<Node> mWatchdogs;

        private Task(DelayQueue<Node> watchdogs) {
            mWatchdogs = watchdogs;
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    Node node = mWatchdogs.take();
                    try {
                        if (node.mWatchdog.isExpired() || !node.mWatchdog.isEnabled()) {
                            continue;
                        }

                        node.mWatchdog.checkFed();
                    } finally {
                        node.reset();
                        mWatchdogs.add(node);
                    }
                } catch (InterruptedException e) {
                    break;
                } catch (Throwable t) {
                    LOGGER.error("Error while checking watchdog", t);
                }
            }
        }
    }

    private static class Node implements Delayed {

        public final InternalWatchdog mWatchdog;

        private long expirationTimeMillis;

        private Node(InternalWatchdog watchdog) {
            mWatchdog = watchdog;
            expirationTimeMillis = 0;
        }

        public void reset() {
            expirationTimeMillis = System.currentTimeMillis() + mWatchdog.getTimeout().valueAsMillis();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diffMs = expirationTimeMillis - System.currentTimeMillis();
            return unit.convert(diffMs, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            long diffMillis = getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS);
            diffMillis = Math.min(diffMillis, 1);
            diffMillis = Math.max(diffMillis, -1);

            return (int) diffMillis;
        }
    }
}
