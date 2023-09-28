package com.flash3388.flashlib.scheduling.actions;

import com.flash3388.flashlib.scheduling.ActionInterface;

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
                new GenericSequentialActionGroup(ActionType.SMALL, 1, params)),
        SMALL_MULTI_SEQ_GROUP((params)->
                new GenericSequentialActionGroup(ActionType.SMALL, 3, params))
        ;

        private final Function<TestActionParams, ActionInterface> mActionGenerator;

        ActionType(Function<TestActionParams, ActionInterface> actionGenerator) {
            mActionGenerator = actionGenerator;
        }

        public ActionInterface create(TestActionParams params) {
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
        public void execute() {
            byte[] bytes = new byte[1024];
            mRandom.nextBytes(bytes);

            output(bytes);
        }
    }

    public static class MediumSizeAction extends TestAction {

        private final Random mRandom;

        private MediumSizeAction(TestActionParams params) {
            super(params);
            mRandom = new Random();
        }

        @Override
        public void execute() {
            Collection<Integer> numbers = IntStream.range(0, 100)
                    .mapToObj((i) -> mRandom.nextInt())
                    .sorted(Integer::compareTo)
                    .collect(Collectors.toList());

            output(numbers);
        }
    }

    public static class SleepingAction extends TestAction {

        private final long mSleepTimeMs;

        private SleepingAction(TestActionParams params, long sleepTimeMs) {
            super(params);
            mSleepTimeMs = sleepTimeMs;
        }

        @Override
        public void execute() {
            try {
                Thread.sleep(mSleepTimeMs);
            } catch (InterruptedException e) {
                output(e);
            }
        }
    }

    public static class GenericSequentialActionGroup extends TestSequentialActionGroup {

        private GenericSequentialActionGroup(ActionType containedType, int actionsCount,
                                             TestActionParams params) {
            super(params);

            IntStream.range(0, actionsCount)
                    .forEach((i)->add(containedType.create(params)));

        }
    }
}
