package com.flash3388.flashlib.robot.scheduling;

import benchmark.benchmark.util.GlobalRandom;
import benchmark.benchmark.util.Range;
import com.beans.Property;
import com.beans.properties.SimpleProperty;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.SequentialActionGroup;
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

import java.util.Collection;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SchedulerBenchmark {

    private static final RobotMode ROBOT_MODE = new RobotMode("test", 1);

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void run_withSpecificLoad_iterationThroughput(SchedulerContext context, Blackhole blackhole) throws Exception {
        context.setBlackhole(blackhole);

        Scheduler scheduler = context.getScheduler();
        scheduler.run(ROBOT_MODE);
    }

    @State(value = Scope.Thread)
    public static class SchedulerContext {

        private final Property<Blackhole> mBlackholeProperty = new SimpleProperty<>();

        @Param({"10", "30"})
        private int mActionsCount;
        @Param({"SMALL", "MEDIUM", "SMALL_SINGLE_SEQ_GROUP", "SMALL_MULTI_SEQ_GROUP"})
        private ActionType mActionType;
        @Param({"SINGLE_THREAD"})
        private SchedulerImpl mSchedulerImpl;

        private Scheduler mScheduler;

        public Scheduler getScheduler() {
            return mScheduler;
        }

        public void setBlackhole(Blackhole blackhole) {
            mBlackholeProperty.set(blackhole);
        }

        @Setup(Level.Invocation)
        public void prepare() {
            mScheduler = mSchedulerImpl.generate();

            Consumer<Object> consumer = (object) -> {
                Blackhole blackhole = mBlackholeProperty.get();
                blackhole.consume(object);
            };

            IntStream.range(0, mActionsCount)
                    .mapToObj((i) -> mActionType.generate(mScheduler, consumer))
                    .forEach(Action::start);
        }
    }

    public enum SchedulerImpl {
        SINGLE_THREAD(()->new SingleThreadScheduler(new SystemNanoClock()))
        ;

        private final Supplier<Scheduler> mGenerator;

        SchedulerImpl(Supplier<Scheduler> generator) {
            mGenerator = generator;
        }

        Scheduler generate() {
            return mGenerator.get();
        }
    }

    public enum ActionType {
        SMALL(SmallSizeAction::new),
        MEDIUM(MediumSizeAction::new),
        SMALL_SINGLE_SEQ_GROUP((scheduler, outputConsumer)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(1, 1), scheduler, outputConsumer)),
        SMALL_MULTI_SEQ_GROUP((scheduler, outputConsumer)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(2, 3), scheduler, outputConsumer))
        ;

        private final BiFunction<Scheduler, Consumer<Object>, Action> mActionGenerator;

        ActionType(BiFunction<Scheduler, Consumer<Object>, Action> actionGenerator) {
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

    private static class MediumSizeAction extends Action {

        private final Consumer<Object> mOutputConsumer;
        private final Random mRandom;

        private MediumSizeAction(Scheduler scheduler, Consumer<Object> outputConsumer) {
            super(scheduler, Time.INVALID);
            mOutputConsumer = outputConsumer;
            mRandom = new Random();
        }

        @Override
        protected void execute() {
            Collection<Integer> numbers = IntStream.range(0, 100)
                    .mapToObj((i) -> mRandom.nextInt())
                    .sorted(Integer::compareTo)
                    .collect(Collectors.toList());

            mOutputConsumer.accept(numbers);
        }

        @Override
        protected void end() { }
    }

    private static class GenericSequentialActionGroup extends SequentialActionGroup {

        private GenericSequentialActionGroup(ActionType containedType, Range amountRange,
                                     Scheduler scheduler, Consumer<Object> outputConsumer) {
            super(scheduler, new SystemNanoClock());

            int actionsCount = GlobalRandom.nextIntInRange(amountRange);
            IntStream.range(0, actionsCount)
                    .forEach((i)->add(containedType.generate(scheduler, outputConsumer)));

        }
    }
}
