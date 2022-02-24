package robot;

import com.flash3388.flashlib.scheduling2.Action;
import com.flash3388.flashlib.scheduling2.ActionFlag;
import com.flash3388.flashlib.scheduling2.Configuration;
import com.flash3388.flashlib.scheduling2.Control;
import com.flash3388.flashlib.scheduling2.Requirement;
import com.flash3388.flashlib.scheduling2.Scheduler;
import com.flash3388.flashlib.scheduling2.SchedulerMode;
import com.flash3388.flashlib.scheduling2.imp.SchedulerImpl;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.LogLevel;
import com.flash3388.flashlib.util.logging.LoggerBuilder;

public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new SchedulerImpl(
                new SystemNanoClock(),
                new LoggerBuilder("test")
                        .enableConsoleLogging(true)
                        .setLogLevel(LogLevel.DEBUG)
                        .build()
        );

        System system = new System();
        scheduler.submit(new TestAction())
                .name("test-1")
                .requires(system)
                .timeout(Time.seconds(5))
                .flags(ActionFlag.PREFERRED_FOR_REQUIREMENT)
                .start();

        scheduler.submit(new TestAction())
                .name("test-2")
                .requires(system)
                .timeout(Time.seconds(2))
                .start();

        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                break;
            }

            scheduler.run(new SchedulerMode() {
                @Override
                public boolean isDisabled() {
                    return false;
                }
            });
        }
    }

    private static class System implements Requirement {

    }

    private static class TestAction implements Action {

        @Override
        public void configure(Configuration configuration) {

        }

        @Override
        public void initialize(Control control) {

        }

        @Override
        public void execute(Control control) {

        }

        @Override
        public void end(Control control) {

        }
    }
}
