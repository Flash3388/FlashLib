package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.EmptyAction;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Thread)
public class SchedulerStartBenchmark {

    private static final SchedulerMode SCHEDULER_MODE = new SchedulerModeImpl(false);

    @Param({"SINGLE_THREAD", "NEW_SINGLE_THREAD"})
    private SchedulerImpl mSchedulerImpl;
    @Param({"NO_CONFLICTS", "ONE_CONFLICTS", "THREE_CONFLICTS"})
    private ConflictState mConflictState;

    private Scheduler mScheduler;
    private Action mAction;

    @Setup(Level.Invocation)
    public void setup() {
        mScheduler = mSchedulerImpl.create();
        mAction = new EmptyAction(mScheduler);
        mConflictState.generateConflicts(mScheduler, mAction);

        mScheduler.run(SCHEDULER_MODE);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void startAction_withEmptyAction_withConflictState() {
        mAction.start();
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void startActionAndIterate_withEmptyAction_withConflictState() {
        mAction.start();
        mScheduler.run(SCHEDULER_MODE);
    }

    public enum ConflictState {
        NO_CONFLICTS(0),
        ONE_CONFLICTS(1),
        THREE_CONFLICTS(3)
        ;

        private final int mConflictCount;

        ConflictState(int conflictCount) {
            mConflictCount = conflictCount;
        }

        void generateConflicts(Scheduler scheduler, Action action) {
            for (int i = 0; i < mConflictCount; i++) {
                ConflictState.generateSingleRequirement(scheduler, action);
            }
        }

        private static void generateSingleRequirement(Scheduler scheduler, Action action) {
            Requirement requirement = new EmptyRequirement();
            action.configure()
                    .requires(requirement)
                    .save();

            Action conflictingAction = new EmptyAction(scheduler);
            conflictingAction.configure()
                    .requires(requirement)
                    .save();
            conflictingAction.start();
        }
    }
}
