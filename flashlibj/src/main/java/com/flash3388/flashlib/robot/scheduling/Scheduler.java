package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Scheduler is responsible for executing tasks for robots. Users can add {@link Action} and
 * {@link Runnable} objects to the scheduler which will then be executed by when the {@link Scheduler} 
 * runs. This allows for easy management of robot operations.
 * <p>
 * The scheduler can work with simple {@link Runnable} objects, or tasks, which can be added to run once,
 * or run continuously.
 * <p>
 * For more complex operations, the scheduler can use {@link Action} objects. Those objects can be added
 * to the scheduler and then executed as well. Unlike simple tasks, scheduling might depend on {@link Subsystem}
 * objects for operations. The scheduler tracks the required systems of each action making sure that only one 
 * {@link Action} object runs on a {@link Subsystem} at any given time. 
 * <p>
 * In addition, the scheduler can allow {@link Subsystem} to hold default {@link Action} objects, which
 * run only if no {@link Action} is using the {@link Subsystem} at the moment. When the scheduler runs,
 * it checks all registered {@link Subsystem} objects to see if one does not have an action at the moment.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Scheduler {

	private final ActionsRepository mActionsRepository;

	private final SchedulerIteration mSchedulerIteration;

	public Scheduler() {
	    this(Logging.stub());
    }

	public Scheduler(Logger logger) {
        mActionsRepository = new ActionsRepository(logger);

        mSchedulerIteration = new SchedulerIteration(mActionsRepository, logger);
	}

	public void add(Action action) {
        Objects.requireNonNull(action, "action is null");
        mActionsRepository.addAction(action);
	}

    public void setDefaultAction(Subsystem subsystem, Action action) {
        Objects.requireNonNull(subsystem, "subsystem is null");
        Objects.requireNonNull(action, "action is null");

        mActionsRepository.setDefaultActionOnSubsystem(subsystem, action);
    }

    public Optional<Action> getActionRunningOnSubsystem(Subsystem subsystem) {
	    return mActionsRepository.getActionOnSubsystem(subsystem);
    }

	public void stopAllActions() {
        mActionsRepository.removeAllActions();
	}

	public void stopActionsIf(Predicate<Action> removalPredicate) {
        mActionsRepository.removeActionsIf(removalPredicate);
    }

	public void run(RobotMode robotMode) {
        mSchedulerIteration.run(robotMode);
	}
}