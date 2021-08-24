package example;

import com.flash3388.flashlib.global.GlobalDependencies;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.scheduling.threading.MtExecutorServiceWorkers;
import com.flash3388.flashlib.scheduling.threading.MultiThreadedScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.LogLevel;
import com.flash3388.flashlib.util.logging.LoggerBuilder;
import org.slf4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        final int WORKER_COUNT = 2;
        final SchedulerMode RUN_MODE = new SchedulerMode() {
            @Override
            public boolean isDisabled() {
                return false;
            }
        };

        ExecutorService executorService = Executors.newFixedThreadPool(WORKER_COUNT);
        try {
            Clock clock = new SystemNanoClock();
            Logger logger = new LoggerBuilder("example")
                    .enableConsoleLogging(true)
                    .setLogLevel(LogLevel.DEBUG)
                    .build();

            try (MultiThreadedScheduler scheduler = new MultiThreadedScheduler(
                    new MtExecutorServiceWorkers(executorService, WORKER_COUNT),
                    clock,
                    logger)) {
                GlobalDependencies.setClockInstance(clock);
                GlobalDependencies.setSchedulerInstance(scheduler);

                Action action = new ActionBase() {
                    @Override
                    public void execute() {

                    }
                };
                action.configure()
                        .setName("Basic Action")
                        .setTimeout(Time.milliseconds(500))
                        .save();
                action.start();

                Action action1 = new ActionBase() {
                    @Override
                    public void execute() {

                    }
                };
                action1.configure()
                        .setName("Action to Cancel")
                        .save();
                action1.start();

                Time startTime = clock.currentTime();
                Time runTime = Time.seconds(10);
                while (clock.currentTime().sub(startTime).lessThan(runTime)) {
                    scheduler.run(RUN_MODE);
                    Thread.sleep(100);

                    if (action1.isRunning()) {
                        action1.cancel();
                    }
                }
            }
        } finally {
            executorService.shutdownNow();
        }
    }
}
