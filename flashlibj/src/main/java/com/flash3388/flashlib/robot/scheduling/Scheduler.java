package com.flash3388.flashlib.robot.scheduling;

import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.logging.StubLogger;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
 * If it doesn't and a default action is defined, the default action is started. Systems are registered
 * by calling {@link #registerSubsystem(Subsystem)}, but this occurs in the {@link Subsystem} constructor.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Scheduler {

	private final Set<Subsystem> mSubsystems;
	private final Collection<Action> mActions;
	private final Collection<SchedulerTask> mTasks;
	private final Logger mLogger;

	private SchedulerRunMode mRunMode;

	public Scheduler() {
	    this(Logging.stub());
    }

	public Scheduler(Logger logger) {
        mSubsystems = new HashSet<>();
        mActions = new ArrayList<>();
		mTasks = new ArrayList<>();
		mLogger = logger;

        mRunMode = SchedulerRunMode.ALL;
	}

    // FOR TESTING
    /*package*/ Scheduler(Set<Subsystem> subsystems, Collection<Action> actions, Collection<SchedulerTask> tasks, SchedulerRunMode runMode) {
        this(subsystems, actions, tasks, runMode, Logging.stub());
    }

	// FOR TESTING
    /*package*/ Scheduler(Set<Subsystem> subsystems, Collection<Action> actions, Collection<SchedulerTask> tasks, SchedulerRunMode runMode, Logger logger) {
        mSubsystems = subsystems;
        mActions = actions;
        mTasks = tasks;
        mRunMode = runMode;
        mLogger = logger;
    }

	public void setRunMode(SchedulerRunMode runMode) {
		mRunMode = Objects.requireNonNull(runMode, "runMode is null");
	}

	public SchedulerRunMode getRunMode() {
		return mRunMode;
	}

	public void add(SchedulerTask task) {
        Objects.requireNonNull(task, "task is null");
		mTasks.add(task);
	}

	public void add(Action action) {
        Objects.requireNonNull(action, "action is null");

        if (mActions.contains(action)) {
            throw new IllegalArgumentException("action already running");
        }

		updateRequirementsWithNewRunningAction(action);

		mActions.add(action);
	}

	public boolean remove(SchedulerTask task) {
		return mTasks.remove(task);
	}

	public void removeAllTasks() {
		mTasks.clear();
	}

	public boolean remove(Action action) {
        Objects.requireNonNull(action, "action is null");

		if (mActions.remove(action)) {
			action.removed();
			updateRequirementsNoCurrentAction(action);

			return true;
		}

		return false;
	}

	public void removeAllActions() {
		List<Action> allActions = new ArrayList<>(mActions);

		for (Action action : allActions) {
			remove(action);
		}
	}

	public void registerSubsystem(Subsystem subsystem) {
        Objects.requireNonNull(subsystem, "subsystem is null");

		if (mSubsystems.contains(subsystem)) {
			throw new IllegalArgumentException("subsystem already registered");
		}

		mSubsystems.add(subsystem);
	}

	public void run() {
		if (mRunMode.shouldRunTasks()) {
			runTasks();
		}

		if (mRunMode.shouldRunActions()) {
			runActions();
			startDefaultSubsystemActions();
		}
	}

	private void runTasks() {
		List<SchedulerTask> tasks = new ArrayList<>(mTasks);

		for (SchedulerTask task : tasks) {
			if (!task.run()) {
				remove(task);
			}
		}
	}

	private void runActions() {
		List<Action> actions = new ArrayList<>(mActions);

		for (Action action : actions) {
			if (!action.run()) {
				remove(action);
			}
		}
	}

	private void startDefaultSubsystemActions() {
		for (Subsystem subsystem : mSubsystems) {
			if (!subsystem.hasCurrentAction() && subsystem.hasDefaultAction()) {
			    mLogger.debug("Starting default action for {}", subsystem.toString());
				subsystem.startDefaultAction();
			}
		}
	}

	private void updateRequirementsWithNewRunningAction(Action action) {
		for (Subsystem subsystem : action.getRequirements()) {
			if (subsystem.hasCurrentAction()) {
			    Action currentAction = subsystem.getCurrentAction();
				currentAction.cancel();

                mLogger.warn("Requirements conflict in Scheduler between {} and new action {} over subsystem {}",
                        currentAction.toString(), action.toString(), subsystem.toString());
			}

			subsystem.setCurrentAction(action);
		}
	}

	private void updateRequirementsNoCurrentAction(Action action) {
		for (Subsystem subsystem : action.getRequirements()) {
			subsystem.setCurrentAction(null);
		}
	}
}