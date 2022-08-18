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
public class SchedulerStopBenchmark {

    private static final SchedulerMode SCHEDULER_MODE = new SchedulerModeImpl(false);

    @Param({"SINGLE_THREAD", "NEW_SINGLE_THREAD"})
    private SchedulerImpl mSchedulerImpl;
    @Param({"NO_REQUIREMENTS", "ONE_REQUIREMENTS", "THREE_REQUIREMENTS"})
    private RequirementState mRequirementState;

    private Scheduler mScheduler;
    private Action mAction;

    @Setup(Level.Iteration)
    public void setup() {
        mScheduler = mSchedulerImpl.create();
        mAction = new EmptyAction(mScheduler);
        mRequirementState.setupRequirements(mScheduler, mAction);

        mAction.start();
        mScheduler.run(SCHEDULER_MODE);
    }

    @Benchmark
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime})
    public void cancelAndIterate_withEmptyAction_withRequirements() {
        mAction.cancel();
        mScheduler.run(SCHEDULER_MODE);
    }

    public enum RequirementState {
        NO_REQUIREMENTS {
            @Override
            void setupRequirements(Scheduler scheduler, Action action) { }
        },
        ONE_REQUIREMENTS {
            @Override
            void setupRequirements(Scheduler scheduler, Action action) {
                Requirement requirement = new EmptyRequirement();
                action.configure()
                        .requires(requirement)
                        .save();
            }
        },
        THREE_REQUIREMENTS {
            @Override
            void setupRequirements(Scheduler scheduler, Action action) {
                for (int i = 0; i < 3; i++) {
                    Requirement requirement = new EmptyRequirement();
                    action.configure()
                            .requires(requirement)
                            .save();
                }
            }
        }
        ;

        abstract void setupRequirements(Scheduler scheduler, Action action);
    }
}
