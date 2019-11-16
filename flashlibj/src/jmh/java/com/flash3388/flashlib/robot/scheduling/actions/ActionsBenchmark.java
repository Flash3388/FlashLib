package com.flash3388.flashlib.robot.scheduling.actions;

import com.beans.Property;
import com.beans.properties.SimpleProperty;
import com.flash3388.flashlib.robot.scheduling.EmptyScheduler;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
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

@State(Scope.Thread)
public class ActionsBenchmark {

    private Property<Blackhole> mBlackholeProperty = new SimpleProperty<>();

    @Param({"SMALL", "MEDIUM", "SHORT_SLEEP", "SMALL_SINGLE_SEQ_GROUP", "SMALL_MULTI_SEQ_GROUP"})
    private TestActions.ActionType mActionType;

    private Action mAction;

    @Setup(Level.Trial)
    public void setup() {
        Scheduler scheduler = new EmptyScheduler();

        Consumer<Object> consumer = (object) -> {
            Blackhole blackhole = mBlackholeProperty.get();
            blackhole.consume(object);
        };

        mAction = mActionType.create(new TestActionParams(scheduler, consumer));
        mAction.initialize();
    }

    @Benchmark
    @BenchmarkMode({Mode.AverageTime, Mode.Throughput})
    public void execute_forTestAction(Blackhole blackhole) {
        mBlackholeProperty.set(blackhole);

        mAction.execute();
    }
}
