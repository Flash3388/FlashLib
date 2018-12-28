package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.resources.Resource;
import edu.flash3388.flashlib.util.resources.ResourceHolder;

/**
 * <p>
 *     The base class for robot main classes.
 * </p>
 * <p>
 *     Inheriting robot classes need to implement the following methods:
 *     <ul>
 *         <li>{@link #robotInit()}: called when the robot is initialized.
 *         Should be used to initialize robot components</li>
 *         <li>{@link #robotMain()}: the robot main method. Called after initialization,
 *         and should implement the robot logic.</li>
 *         <li>{@link #robotShutdown()}: called after {@link #robotMain()} is finished. Used
 *         for freeing resources and components initialized in {@link #robotInit()}.</li>
 *     </ul>
 * </p>
 * <p>
 *     Use {@link #registerResources(Resource...)} to register {@link Resource}s to be
 *     freed automatically after {@link #robotShutdown()}.
 * </p>
 * <p>
 *     {@link #robotInit()} can throw {@link RobotInitializationException} if an error
 *     has occurred while initializing the robot. This will leave to a shutdown of the robot.
 *     If an exception is thrown from {@link #robotInit()}, all resources registered using
 *     {@link #registerResources(Resource...)} are freed.
 * </p>
 * <p>
 *     If {@link #robotMain()} throws an exception, {@link #robotShutdown()} is called.
 * </p>
 *
 * @since FlashLib 1.0.0
 */
public abstract class RobotBase implements RobotInterface {

    private final ResourceHolder mResourceHolder;

	protected RobotBase() {
	    mResourceHolder = ResourceHolder.empty();
	}

    /**
     * <p>
     *     Registers {@link Resource}s to be freed after the robot has finished running.
     * </p>
     * <p>
     *     Resource freeing will occur after {@link #robotShutdown()}, or
     *     if {@link #robotInit()} throws an exception.
     * </p>
     *
     * @param resources resources to register.
     */
	public final void registerResources(Resource... resources) {
	    for (Resource resource : resources) {
	        mResourceHolder.add(resource);
        }
    }

	final void initialize() throws RobotInitializationException {
		//setting the JVM thread priority for this thread. Should be highest possible.
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		registerResources(RobotResources.CLOCK, RobotResources.SCHEDULER);

        try {
            robotInit();
        } catch (RobotInitializationException | RuntimeException e) {
            freeResources();
            throw e;
        }
	}

	final void start() {
		robotMain();
	}

	final void stop() {
		try {
            robotShutdown();
        } finally {
            freeResources();
        }
	}

	private void freeResources() {
        mResourceHolder.freeAll();
    }
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------

	/**
	 * Called when robot initialization starts, allowing for initialization of user code.
	 *
	 * @throws RobotInitializationException if an error occurs while initializing
	 */
	protected abstract void robotInit() throws RobotInitializationException;

	/**
	 * Called when {@link RobotBase} finished initialization and the robot can be started. 
	 * This is the main method of the robot and all operations should be directed from here.
	 */
	protected abstract void robotMain();

	/**
	 * Called when the robot finishes running, allowing to perform custom stop operations. Should be used
	 * to free robot components.
	 */
	protected abstract void robotShutdown();
}
