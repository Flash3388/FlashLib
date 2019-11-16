package com.flash3388.flashlib.robot.scheduling.actions;

import benchmark.benchmark.util.GlobalRandom;
import benchmark.benchmark.util.Range;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;

import java.util.Collection;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestActions {

    public enum ActionType {
        SMALL(SmallSizeAction::new),
        MEDIUM(MediumSizeAction::new),
        SHORT_SLEEP((scheduler, outputConsumer)->
                new SleepingAction(scheduler, 1L)),
        SMALL_SINGLE_SEQ_GROUP((scheduler, outputConsumer)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(1, 1), scheduler, outputConsumer)),
        SMALL_MULTI_SEQ_GROUP((scheduler, outputConsumer)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(2, 3), scheduler, outputConsumer))
        ;

        private final BiFunction<Scheduler, Consumer<Object>, Action> mActionGenerator;

        ActionType(BiFunction<Scheduler, Consumer<Object>, Action> actionGenerator) {
            mActionGenerator = actionGenerator;
        }

        public Action generate(Scheduler scheduler, Consumer<Object> outputConsumer) {
            return mActionGenerator.apply(scheduler, outputConsumer);
        }
    }

    public static class SmallSizeAction extends Action {

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

    public static class MediumSizeAction extends Action {

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

    public static class SleepingAction extends Action {

        private final long mSleepTimeMs;

        private SleepingAction(Scheduler scheduler, long sleepTimeMs) {
            super(scheduler);
            mSleepTimeMs = sleepTimeMs;
        }

        @Override
        protected void execute() {
            try {
                Thread.sleep(mSleepTimeMs);
            } catch (InterruptedException e) { }
        }

        @Override
        protected void end() { }
    }

    public static class GenericSequentialActionGroup extends SequentialActionGroup {

        private GenericSequentialActionGroup(ActionType containedType, Range amountRange,
                                             Scheduler scheduler, Consumer<Object> outputConsumer) {
            super(scheduler, new SystemNanoClock());

            int actionsCount = GlobalRandom.nextIntInRange(amountRange);
            IntStream.range(0, actionsCount)
                    .forEach((i)->add(containedType.generate(scheduler, outputConsumer)));

        }
    }
}
