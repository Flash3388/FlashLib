package edu.flash3388.flashlib.robot.scheduling;

import java.util.*;

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
public final class Scheduler {

	private static Scheduler mInstance;

	public static Scheduler getInstance() {
		if (mInstance == null) {
			mInstance = new Scheduler();
		}

		return mInstance;
	}

	private final Set<Subsystem> mSubsystems;
	private final Collection<Action> mActions;
	private final Collection<Task> mTasks;

	private SchedulerRunMode mRunMode;

	private Scheduler() {
        mSubsystems = new HashSet<Subsystem>();
        mActions = new ArrayList<Action>();
		mTasks = new ArrayList<Task>();

        mRunMode = SchedulerRunMode.ALL;
	}

	// FOR TESTING
    /*package*/ Scheduler(Set<Subsystem> subsystems, Collection<Action> actions, Collection<Task> tasks, SchedulerRunMode runMode) {
        mSubsystems = subsystems;
        mActions = actions;
        mTasks = tasks;
        mRunMode = runMode;
    }

	public void setRunMode(SchedulerRunMode runMode) {
		mRunMode = runMode;
	}

	public SchedulerRunMode getRunMode() {
		return mRunMode;
	}

	public void execute(Runnable runnable) {
		mTasks.add(new Task(runnable, false));
	}

	public void add(Runnable task) {
		mTasks.add(new Task(task, true));
	}

	public void add(Action action) {
		if (mActions.contains(action)) {
			throw new IllegalArgumentException("action already added");
		}

		updateRequirementsWithNewRunningAction(action);

		mActions.add(action);
	}

	public boolean remove(Runnable runnable) {
		Task taskToRemove = null;

		for (Task task : mTasks) {
			if (task.mRunnable.equals(runnable)) {
				taskToRemove = task;
				break;
			}
		}

		if (taskToRemove != null) {
			mTasks.remove(taskToRemove);
			return true;
		}

		return false;
	}

	public void removeAllTasks() {
		mTasks.clear();
	}

	public boolean remove(Action action) {
		if (mActions.remove(action)) {
			action.removed();
			updateRequirementsNoCurrentAction(action);

			return true;
		}

		return false;
	}

	public void removeAllActions() {
		List<Action> allActions = new ArrayList<Action>(mActions);

		for (Action action : allActions) {
			remove(action);
		}
	}

	public void registerSubsystem(Subsystem subsystem) {
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
		List<Task> tasks = new ArrayList<Task>(mTasks);

		for (Task task : tasks) {
			task.run();

			if (!task.isRepeating()) {
				mTasks.remove(task);
			}
		}
	}

	private void runActions() {
		List<Action> actions = new ArrayList<Action>(mActions);

		for (Action action : actions) {
			if (!action.run()) {
				remove(action);
			}
		}
	}

	private void startDefaultSubsystemActions() {
		for (Subsystem subsystem : mSubsystems) {
			if (!subsystem.hasCurrentAction()) {
				subsystem.startDefaultAction();
			}
		}
	}

	private void updateRequirementsWithNewRunningAction(Action action) {
		for (Subsystem subsystem : action.getRequirements()) {
			if (subsystem.hasCurrentAction()) {
				subsystem.cancelCurrentAction();
			}
			subsystem.setCurrentAction(action);
		}
	}

	private void updateRequirementsNoCurrentAction(Action action) {
		for (Subsystem subsystem : action.getRequirements()) {
			subsystem.setCurrentAction(null);
		}
	}

	/*package*/ static class Task {

		private final Runnable mRunnable;
		private final boolean mIsRepeating;

		Task(Runnable runnable, boolean isRepeating) {
			mRunnable = runnable;
			mIsRepeating = isRepeating;
		}

		void run() {
			mRunnable.run();
		}

		boolean isRepeating() {
			return mIsRepeating;
		}
	}
}