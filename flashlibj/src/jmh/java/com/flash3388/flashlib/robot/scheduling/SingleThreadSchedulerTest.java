package com.flash3388.flashlib.robot.scheduling;

import com.beans.Property;
import com.beans.properties.SimpleProperty;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class SingleThreadSchedulerTest {

    private static final RobotMode ROBOT_MODE = new RobotMode("test", 1);

    @State(value = Scope.Thread)
    public static class SchedulerContext {

        private final Property<Blackhole> mBlackholeProperty;

        @Param({"10", "30"})
        private int mActionsCount;
        @Param({"SMALL"})
        private ActionSize mActionSize;

        private Scheduler mScheduler;

        public SchedulerContext() {
            mBlackholeProperty = new SimpleProperty<>();
        }

        public Scheduler getScheduler() {
            return mScheduler;
        }

        public void setBlackhole(Blackhole blackhole) {
            mBlackholeProperty.set(blackhole);
        }

        @Setup(Level.Invocation)
        public void prepare() {
            Clock clock = new SystemNanoClock();
            mScheduler = new SingleThreadScheduler(clock);

            Consumer<Object> consumer = (object) -> {
                Blackhole blackhole = mBlackholeProperty.get();
                blackhole.consume(object);
            };

            IntStream.range(0, mActionsCount)
                    .mapToObj((i) -> mActionSize.generate(mScheduler, consumer))
                    .forEach(Action::start);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void run_withSpecificLoad_iterationThroughput(SchedulerContext context, Blackhole blackhole) throws Exception {
        context.setBlackhole(blackhole);

        Scheduler scheduler = context.getScheduler();
        scheduler.run(ROBOT_MODE);
    }

    public enum ActionSize {
        SMALL(SmallSizeAction::new)
        ;

        private final BiFunction<Scheduler, Consumer<Object>, Action> mActionGenerator;

        ActionSize(BiFunction<Scheduler, Consumer<Object>, Action> actionGenerator) {
            mActionGenerator = actionGenerator;
        }

        Action generate(Scheduler scheduler, Consumer<Object> outputConsumer) {
            return mActionGenerator.apply(scheduler, outputConsumer);
        }
    }

    private static class SmallSizeAction extends Action {

        private final Consumer<Object> mOutputConsumer;
        private final Random mRandom;

        private SmallSizeAction(Scheduler scheduler, Consumer<Object> outputConsumer) {
            super(scheduler, Time.INVALID);
            mOutputConsumer = outputConsumer;
            mRandom = new Random();
        }

        @Override
        protected void execute() {
            byte[] bytes = new byte[1024];
            mRandom.nextBytes(bytes);

            mOutputConsumer.accept(bytes);
        }

        @Override
        protected void end() { }
    }
}
