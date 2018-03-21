package edu.flash3388.flashlib.robot;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.devices.IOFactory;
import edu.flash3388.flashlib.robot.hal.HAL;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * RobotBase provides the base for robots. It contains the robot's main method which should be called when 
 * starting the robot software. When the robot is started, the user implementation of this class is initialized,
 * robot and FlashLib systems are initialized and user robot code is then started by calling {@link #robotMain()}.
 * When the JVM enters shutdown, this class uses a shutdown hook to perform ordered robot shutdown and will
 * allow custom user shutdown by calling {@link #robotShutdown()}.
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
 * To allow user customization, it is possible to override {@link #configInit(RobotInitializer)} and customize
 * initialization parameters. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class RobotBase implements SBC, RobotInterface{
	
	/**
	 * The base class for robot initialization customization. Holds variables with initialization
	 * values. Change those values according to the variables documentation to customize initialization.
	 * <p>
	 * If providing custom robot base which extends {@link RobotBase}, it is possible to create an
	 * extended initializer class which extends this class, adding new parameters for the initialization.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.1
	 */
	protected static class RobotInitializer{
		/**
		 * Indicates the initialization mode for HAL. This value will be used differently 
		 * depending on the used HAL implementation.
		 * <p>
		 * The default value is `0`.
		 */
		public int halInitMode = 0;
		/**
		 * Indicates whether or not to initialize HAL. 
		 * <p>
		 * The default value is `true`.
		 */
		public boolean initHAL = false;
		
		/**
		 * Indicates whether or not to initialize robot communications.
		 * <p>
		 * The default value is `false`
		 */
		public boolean initCommunications = false;
		/**
		 * This value contains the {@link CommInterface} to be used for the robot
		 * communications. If {@link #initCommunications} is false, this value will be ignored.
		 * If this value is `null`, communications will not be initialized.
		 * <p>
		 * The default value is `null`.
		 */
		public CommInterface commInterface = null;
		
		/**
		 * Contains the {@link ModeSelector} to be used by the robot for choosing operation modes.
		 * If this value is null, then the operation mode of the robot will always be 
		 * {@link ModeSelector#MODE_DISABLED}.
		 * <p>
		 * The default value is `null`.
		 */
		public ModeSelector modeSelector;
		/**
		 * Contains the {@link HIDInterface} to be used by the HID package.
		 * If this value is null, then no {@link HIDInterface} will be set to {@link RobotFactory}.
		 * <p>
		 * The default value is {@link EmptyHIDInterface}.
		 */
		public HIDInterface hidInterface = new EmptyHIDInterface();
		
		/**
		 * Contains initialization data for Flashboard in the form of {@link FlashboardInitData}.
		 * If this value is `null`, Flashboard control will not be initialized.
		 * <p>
		 * The default value is an instance of {@link FlashboardInitData}.
		 */
		public FlashboardInitData flashboardInitData = new FlashboardInitData();
		/**
		 * Inidicates whether or not to initialize flashboard. If true, flashboard will 
		 * be initialize. If false, flashboard will be initialized.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean initFlashboard = false;
		
		/**
		 * Copies initialization data from the given initializer to this one.
		 * 
		 * @param initializer initializer to copy.
		 */
		public void copy(RobotInitializer initializer){
			halInitMode = initializer.halInitMode;
			initHAL = initializer.initHAL;
			
			initCommunications = initializer.initCommunications;
			commInterface = initializer.commInterface;
			
			modeSelector = initializer.modeSelector;
			hidInterface = initializer.hidInterface;
			
			flashboardInitData = initializer.flashboardInitData;
			initFlashboard = initializer.initFlashboard;
		}
	}
	
	/**
	 * The name of the manifest attribute which holds the name of the user's main
	 * robot class.
	 */
	public static final String MANIFEST_ROBOT_CLASS = "Robot-Class";
	
	/**
	 * FlashLib's main log. Received by calling {@link FlashUtil#getLogger()}. Used to log
	 * initialization and error data for software operation tracking.
	 */
	protected static final Logger logger = FlashUtil.getLogger();
	
	private static RobotBase userImplement;
	private static boolean halInitialized = false;
	
	private ModeSelector modeSelector;
	private Communications communications;
	private LocalShell shell = new LocalShell();
	
	
	//--------------------------------------------------------------------
	//----------------------------MAIN------------------------------------
	//--------------------------------------------------------------------

	public static void main(String[] args){
		//setting the JVM thread priority for this thread. Should be highest possible.
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		//adding shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread(()->onShutdown()));
		
		//loading user class
		try {
			userImplement = loadUserClass();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
			logger.log(Level.SEVERE, "Exception occurred while loading user class", e);
		}
		if(userImplement == null){
			logger.severe("Failed to initialize user robot implementation");
			shutdown(1);
		}
		
		try{
			//setting up robot systems
			setupRobot();
		}catch(IOException | SecurityException | InstantiationException e){
			logger.log(Level.SEVERE, "Exception occurred in robot setup", e);
			shutdown(1);
		}
		
		logger.info("Starting robot");
		try{
			//starting robot
			userImplement.robotMain();
		}catch(Throwable t){
			logger.log(Level.SEVERE, "Exception occurred in robot setup", t);
			shutdown(1);
		}
	}
	private static RobotBase loadUserClass() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException{
		//finding user class and instantiating it.
		String robotName = null;
		Enumeration<URL> resources = RobotBase.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
		
	    while (resources.hasMoreElements()) {
			Manifest manifest = new Manifest(resources.nextElement().openStream());
			Attributes attr = manifest.getMainAttributes();
			for (Iterator<Object> iterator = attr.keySet().iterator(); iterator.hasNext();) {
				Object key = iterator.next();
				if(key.toString().equals(MANIFEST_ROBOT_CLASS)){
					robotName = attr.get(key).toString().trim();
					break;
				}
			}
	    }
	    
	    if(robotName != null){
			logger.info("User class found: "+robotName);
			return (RobotBase) Class.forName(robotName).newInstance();
	    }
		return null;
	}
	private static void setupRobot() throws SecurityException, IOException, InstantiationException {
		RobotInitializer initializer = new RobotInitializer();
		//allowing user to provide custom configuration before init
		userImplement.configInit(initializer);
		
		//initializing FlashLib for robot operation
		FlashRobotUtil.initFlashLib(userImplement, initializer.hidInterface, 
				initializer.initFlashboard? initializer.flashboardInitData : null);
		
		//setting up the mode selector for robot operations
		if(initializer.modeSelector != null){
			userImplement.modeSelector = initializer.modeSelector;
		}else{
			logger.warning("Mode selector was not provided");
		}
		
		//initializing HAL if user wants to
		if(initializer.initHAL){
			int halmode = initializer.halInitMode;
			logger.info("Initializing HAL: "+halmode);
			int status = HAL.initializeHAL(halmode);
			if(status != 0){
				throw new InstantiationException("Failed to initialize HAL: "+status);
			}
			logger.info("HAL initialized: "+HAL.boardName());
			halInitialized = true;
			
			IOFactory.setProvider(HAL.createIOProvider());
		}
		
		//initializing communications if the user wants to
		if(initializer.initCommunications){
			if(initializer.commInterface != null){
				logger.info("Initializing robot communications");
				userImplement.communications = new Communications("Robot", initializer.commInterface);
				logger.info("Done");
			}else{
				logger.warning("CommInterface for robot communications is null");
			}
		}
	}
	private static void onShutdown(){
		logger.info("Shuting down...");
		//user shutdown
		if(userImplement != null){
			logger.info("User shutdown...");
			try {
				userImplement.robotShutdown();
			} catch (Throwable t) {
				logger.log(Level.SEVERE, "Exception occurred during user shutdown", t);
			}
			
			//communications shutdown
			if(userImplement.communications != null){
				logger.info("Shutting down robot communications...");
				userImplement.communications.close();
				logger.info("Done");
			}
		}
		
		//flashboard shutdown
		if(Flashboard.flashboardInit()){
			logger.info("Shutting down Flashboard...");
			Flashboard.close();
			logger.info("Done");
		}
		
		//hal shutdown
		if(halInitialized){
			logger.info("Shutting down HAL...");
			HAL.shutdown();
			logger.info("Done");
			halInitialized = false;
		}
		
		//done
		logger.info("Shutdown successful");
		
		for (Handler handler : logger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
	
	/**
	 * Terminates the currently running Java Virtual Machine. 
	 * The argument serves as a status code; by convention, a nonzero status code indicates abnormal termination.
	 * <p>
	 * Calls {@link System#exit(int)}.
	 * <p>
	 * This should be used to terminate the robot software only if necessary. Generally this is not recommended
	 * for robot operations, but if an error in operations has occurd then this can be used to stop operations.
	 * 
	 * @param code the exit mode, used to indicate if the software has terminated with an error or not. Use
	 * a non-zero value to indicate a termination due to error.
	 */
	public static void shutdown(int code){
		System.exit(code);
	}
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets the local shell object to use for execution of shell commands on this platform.
	 * This will be an implementation of {@link Shell} for local operations: {@link LocalShell}.
	 * <p>
	 * Local shell is a {@link Sendable} object, so it is possible to attach it to a {@link Communications}
	 * objects to allow remote shell execution. The counterpart of local shell is {@link RemoteShell}.
	 * 
	 * @return a shell
	 */
	@Override
	public Shell getShell(){
		return shell;
	}
	/**
	 * Gets the robot {@link Communications} object if initialized. Can be used to perform communications
	 * with remote softwares for robot control.
	 * <p>
	 * To initialize, insure that {@link RobotInitializer#initCommunications} is `true` and that a {@link CommInterface}
	 * for communications is provided to {@link RobotInitializer#commInterface} during initialization of the robot.
	 * 
	 * @return robot communications, or null if not initialized
	 */
	@Override
	public Communications getCommunications(){
		return communications;
	}
	
	/**
	 * Gets the initialized {@link ModeSelector} object for the robot. Can be set during initialization 
	 * to {@link RobotInitializer#modeSelector}. 
	 * <p>
	 * This object will be used by base methods for operation mode data.
	 * 
	 * @return robot mode selector, or null if not initialized.
	 */
	@Override
	public ModeSelector getModeSelector(){
		return modeSelector;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * returns false always.
	 * <p>
	 * It is recommended to override this method and indicate when the robot is controlled by a user.
	 */
	@Override
	public boolean isOperatorControl() {
		return false;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * returns false always
	 */
	@Override
	public boolean isFRC(){
		return false;
	}
	
	/**
	 * Used by {@link RobotBase} to allow users to perform custom initialization for 
	 * the robot. This method is called before setting up the robot systems.
	 * <p>
	 * {@link RobotInitializer} contains variables which indicate data about robot initialization. Change
	 * values of those variables in accordance to their documentation to customize initialization.
	 * <p>
	 * When providing a custom base for robots, it is recommended to create a new initialization class
	 * with additional initialization parameters and extend {@link RobotInitializer}.
	 * 
	 * @param initializer initializer object
	 */
	protected void configInit(RobotInitializer initializer){}
	/**
	 * Called when {@link RobotBase} finished initialization and the robot can be started. 
	 * This is the main method of the robot and all operations should be directed from here.
	 */
	protected abstract void robotMain();
	/**
	 * Called when the JVM shuts down to perform custom shutdown operations. Should be used
	 * to free robot systems.
	 */
	protected abstract void robotShutdown();
}
