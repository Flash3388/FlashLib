package com.flash3388.flashlib.util.flow;

import org.junit.Test;

public class SingleUseRunnerTest {

    @Test(expected = IllegalStateException.class)
    public void start_alreadyStarted_throwsIllegalStateException() throws Exception {
        SingleUseRunner runner = new FakeSingleUseRunner();

        runner.start();
        runner.start();
    }

    @Test(expected = IllegalStateException.class)
    public void start_alreadyStopped_throwsIllegalStateException() throws Exception {
        SingleUseRunner runner = new FakeSingleUseRunner();

        runner.start();
        runner.stop();
        runner.start();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_alreadyStopped_throwsIllegalStateException() throws Exception {
        SingleUseRunner runner = new FakeSingleUseRunner();

        runner.start();
        runner.stop();
        runner.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stop_notStarted_throwsIllegalStateException() throws Exception {
        SingleUseRunner runner = new FakeSingleUseRunner();

        runner.stop();
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