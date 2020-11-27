package com.flash3388.flashlib.scheduling;

import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.scheduling.actions.ActionsMock;
import com.flash3388.flashlib.scheduling.actions.SynchronousActionContext;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.ServiceIntervalExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SeparateThreadSchedulerTest {


    private static final SchedulerMode DISABLED_MODE = new SchedulerMode() {
        @Override
        public boolean isDisabled() {
            return true;
        }
    };
    private static final SchedulerMode NON_DISABLED_MODE = new SchedulerMode() {
        @Override
        public boolean isDisabled() {
            return false;
        }
    };
    private static final Time TASK_ASSERTION_WAIT = Time.milliseconds(1000);

    private Time mRunInterval;
    private ExecutorService mExecutorService;
    private Clock mClock;
    private Logger mLogger;

    private StsUserRequests mUserRequests;
    private StsSchedulerStatus mSchedulerStatus;

    private StsSchedulingTask mSchedulingTask;

    private Set<Action> mRunningActions;
    private Map<Requirement, Action> mRunningOnRequirements;
    private Map<Subsystem, Action> mDefaultActions;

    private Set<Action> mMarkedForCancel;
    private Set<Action> mCancelReplaceActions;

    @BeforeEach
    public void setUp() throws Exception {
        mRunInterval = Time.milliseconds(10);
        mExecutorService = Executors.newFixedThreadPool(2);
        mClock = mock(Clock.class);
        mLogger = mock(Logger.class);

        mUserRequests = new StsUserRequests();
        mSchedulerStatus = new StsSchedulerStatus();
        mRunningActions = new HashSet<>();
        mRunningOnRequirements = new HashMap<>();
        mDefaultActions = new HashMap<>();
        mMarkedForCancel = new HashSet<>();
        mCancelReplaceActions = new HashSet<>();
    }

    @AfterEach
    public void tearDown() throws Exception {
        mExecutorService.shutdownNow();
    }

    @Test
    public void start_withAction_actionsSavedAsRunningAndTransferredToTask() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = createScheduler();
        scheduler.run(NON_DISABLED_MODE);
        scheduler.start(action);

        assertInTask(TASK_ASSERTION_WAIT, (task)-> {
            assertThat(task.getActionsContexts(), hasKey(action));
            SynchronousActionContext context = task.getActionsContexts().get(action);
            assertTrue(context.isStarted());
        });
    }

    @Test
    public void start_withAction_actionBeingExecuted() throws Exception {
        Action action = ActionsMock.actionMocker()
                .build();

        Scheduler scheduler = createScheduler();
        scheduler.run(NON_DISABLED_MODE);
        scheduler.start(action);

        assertInTask(TASK_ASSERTION_WAIT, (task)-> {
            verify(action, atLeastOnce()).execute();
        });
    }

    private void assertInTask(Time maxWaitTime, Consumer<StsSchedulingTask> task) {
        Future<AssertionError> future = mExecutorService.submit(()-> {
            long waitTime = maxWaitTime.valueAsMillis();
            long sleepTime = maxWaitTime.valueAsMillis() / 4;
            AssertionError lastError = null;
            while(!Thread.interrupted() && waitTime > 0) {
                try {
                    task.accept(mSchedulingTask);
                    lastError = null;
                } catch (AssertionError e) {
                    lastError = e;
                }

                Thread.sleep(sleepTime);
                waitTime -= sleepTime;
            }

            return lastError;
        });

        try {
            AssertionError error = future.get();
            if (error != null) {
                throw error;
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private Scheduler createScheduler() {
        Executor executor = new ServiceIntervalExecutor(mExecutorService, mRunInterval) {
            @Override
            public void execute(Runnable command) {
                super.execute(command);
                mSchedulingTask = (StsSchedulingTask)command;
            }
        };

        return new SeparateThreadScheduler(executor,
                mClock, mLogger, mUserRequests, mSchedulerStatus,
                mRunningActions, mRunningOnRequirements, mDefaultActions);
    }
}