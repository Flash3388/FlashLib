package edu.flash3388.flashlib.robot;

import java.util.logging.Logger;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * RobotBase provides the base for robots. It contains the robot's main method which should be called when 
 * starting the robot software. When the robot is started, the user implementation of this class is initialized,
 * robot and FlashLib systems are initialized and user robot code is then started by calling {@link #robotMain()}.
 * When the JVM enters stop, this class uses a stop hook to perform ordered robot stop and will
 * allow custom user stop by calling {@link #robotShutdown()}.
 * 
 * <p>
 * To setup a robot class, insure the software's MANIFEST file contains a {@value #MANIFEST_ROBOT_CLASS} property
 * and the value should be the user robot classname and package (full package.classname). Remember that the user
 * robot class should inherit this base or extend a class which already inherited this.
 * <p>
 * This class implements the interfaces {@link RobotInterface} and {@link SBC} and provides basic implementations. Those
 * can be overridden if needed.
 * <p>
 * There are 2 abstract method: {@link #robotMain()} and {@link #robotShutdown()}. Those must recieve implementation.
 * To allow user customization, it is possible to override {@link #configInit(RobotInitializationData)} and customize
 * initialization parameters. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class RobotBase implements RobotInterface {
	
	/**
	 * FlashLib's main log. Received by calling {@link FlashUtil#getLogger()}. Used to log
	 * initialization and error data for software operation tracking.
	 */
	protected final Logger logger = FlashUtil.getLogger();

	protected RobotBase() {

	}

	final void initialize() throws RobotInitializationException {
		//setting the JVM thread priority for this thread. Should be highest possible.
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        robotInit();
	}

	final void start() {
		robotMain();
	}

	final void stop() {
		robotShutdown();
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
