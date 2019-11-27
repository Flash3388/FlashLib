package com.flash3388.flashlib.util.flow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleUseRunnerTest {

    @Test
    public void start_alreadyStarted_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()->{
            SingleUseRunner runner = new FakeSingleUseRunner();

            runner.start();
            runner.start();
        });
    }

    @Test
    public void start_alreadyStopped_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()->{
            SingleUseRunner runner = new FakeSingleUseRunner();

            runner.start();
            runner.stop();
            runner.start();
        });
    }

    @Test
    public void stop_alreadyStopped_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()->{
            SingleUseRunner runner = new FakeSingleUseRunner();

            runner.start();
            runner.stop();
            runner.stop();
        });
    }

    @Test
    public void stop_notStarted_throwsIllegalStateException() throws Exception {
        assertThrows(IllegalStateException.class, ()->{
            SingleUseRunner runner = new FakeSingleUseRunner();

            runner.stop();
        });
    }

    private static class FakeSingleUseRunner extends SingleUseRunner {

        @Override
        protected void startRunner() {
        }

        @Override
        protected void stopRunner() {
        }
    }
}