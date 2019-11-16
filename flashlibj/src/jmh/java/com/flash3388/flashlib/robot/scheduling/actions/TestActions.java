package com.flash3388.flashlib.robot.scheduling.actions;

import benchmark.benchmark.util.GlobalRandom;
import benchmark.benchmark.util.Range;

import java.util.Collection;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestActions {

    public enum ActionType {
        SMALL(SmallSizeAction::new),
        MEDIUM(MediumSizeAction::new),
        SHORT_SLEEP((params)->
                new SleepingAction(params, 1L)),
        SMALL_SINGLE_SEQ_GROUP((params)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(1, 1), params)),
        SMALL_MULTI_SEQ_GROUP((params)->
                new GenericSequentialActionGroup(ActionType.SMALL, new Range(2, 3), params))
        ;

        private final Function<TestActionParams, Action> mActionGenerator;

        ActionType(Function<TestActionParams, Action> actionGenerator) {
            mActionGenerator = actionGenerator;
        }

        public Action create(TestActionParams params) {
            return mActionGenerator.apply(params);
        }
    }

    public static class SmallSizeAction extends TestAction {

        private final Random mRandom;

        private SmallSizeAction(TestActionParams params) {
            super(params);
            mRandom = new Random();
        }

        @Override
        protected void execute() {
            byte[] bytes = new byte[1024];
            mRandom.nextBytes(bytes);

            output(bytes);
        }

        @Override
        protected void end() { }
    }

    public static class MediumSizeAction extends TestAction {

        private final Random mRandom;

        private MediumSizeAction(TestActionParams params) {
            super(params);
            mRandom = new Random();
        }

        @Override
        protected void execute() {
            Collection<Integer> numbers = IntStream.range(0, 100)
                    .mapToObj((i) -> mRandom.nextInt())
                    .sorted(Integer::compareTo)
                    .collect(Collectors.toList());

            output(numbers);
        }

        @Override
        protected void end() { }
    }

    public static class SleepingAction extends TestAction {

        private final long mSleepTimeMs;

        private SleepingAction(TestActionParams params, long sleepTimeMs) {
            super(params);
            mSleepTimeMs = sleepTimeMs;
        }

        @Override
        protected void execute() {
            try {
                Thread.sleep(mSleepTimeMs);
            } catch (InterruptedException e) {
                output(e);
            }
        }

        @Override
        protected void end() { }
    }

    public static class GenericSequentialActionGroup extends TestSequentialActionGroup {

        private GenericSequentialActionGroup(ActionType containedType, Range amountRange,
                                             TestActionParams params) {
            super(params);

            int actionsCount = GlobalRandom.nextIntInRange(amountRange);
            IntStream.range(0, actionsCount)
                    .forEach((i)->add(containedType.create(params)));

        }
    }
}
