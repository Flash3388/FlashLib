package com.flash3388.flashlib.robot.scheduling;

import com.beans.Property;
import com.beans.properties.SimpleProperty;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.scheduling.actions.TestActionParams;
import com.flash3388.flashlib.robot.scheduling.actions.TestActions;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.util.function.Consumer;
import java.util.stream.IntStream;

@State(Scope.Thread)
public class SchedulerExecutionBenchmark {

    private static final RobotMode ROBOT_MODE = RobotMode.create("test", 1);

    private Property<Blackhole> mBlackholeProperty = new SimpleProperty<>();

    @Param({"10", "30"})
    private int mActionsCount;
    @Param({"SMALL", "MEDIUM", "SHORT_SLEEP", "SMALL_SINGLE_SEQ_GROUP", "SMALL_MULTI_SEQ_GROUP"})
    private TestActions.ActionType mActionType;
    @Param({"SINGLE_THREAD"})
    private SchedulerImpl mSchedulerImpl;

    private Scheduler mScheduler;

    @Setup(Level.Trial)
    public void setup() {
        mScheduler = mSchedulerImpl.create();

        Consumer<Object> consumer = (object) -> {
            Blackhole blackhole = mBlackholeProperty.get();
            blackhole.consume(object);
        };

        IntStream.range(0, mActionsCount)
                .mapToObj((i) -> mActionType.create(new TestActionParams(mScheduler, consumer)))
                .forEach(Action::start);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public void run_withSpecificLoad_iterationThroughput(Blackhole blackhole) throws Exception {
        mBlackholeProperty.set(blackhole);

        Scheduler scheduler = mScheduler;
        scheduler.run(ROBOT_MODE);
    }
}
