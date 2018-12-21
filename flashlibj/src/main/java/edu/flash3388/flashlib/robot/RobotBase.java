package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.resources.Resource;
import edu.flash3388.flashlib.util.resources.ResourceHolder;

/**
 * RobotBase provides the base for robots. It contains the robot's main method which should be called when 
 * starting the robot software. When the robot is started, the user implementation of this class is initialized,
 * robot and FlashLib systems are initialized and user robot code is then started by calling {@link #robotMain()}.
 * When the JVM enters stop, this class uses a stop hook to perform ordered robot stop and will
 * allow custom user stop by calling {@link #robotShutdown()}.
 *
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class RobotBase implements RobotInterface {

    private final ResourceHolder mResourceHolder;

	protected RobotBase() {
	    mResourceHolder = ResourceHolder.empty();
	}

	public final void registerResources(Resource... resources) {
	    for (Resource resource : resources) {
	        mResourceHolder.add(resource);
        }
    }

	final void initialize() throws RobotInitializationException {
		//setting the JVM thread priority for this thread. Should be highest possible.
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        try {
            robotInit();
        } catch (RobotInitializationException | RuntimeException e) {
            stop();
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
            mResourceHolder.freeAll();

            RobotResources.CLOCK.clear();
            RobotResources.SCHEDULER.clear();
        }
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
	 * to free robot systems.
	 */
	protected abstract void robotShutdown();
}
