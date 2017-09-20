package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.EmptyHIDInterface;
import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.hal.HAL;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class RobotBase implements SBC{
	
	protected static class BasicInitializer{
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
		public boolean initHAL = true;
		
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
		
		public void copy(BasicInitializer initializer){
			halInitMode = initializer.halInitMode;
			initHAL = initializer.initHAL;
			
			initCommunications = initializer.initCommunications;
			commInterface = initializer.commInterface;
		}
	}
	protected static class RobotInitializer extends BasicInitializer{
		/**
		 * Contains the {@link HIDInterface} to be used by the HID package.
		 * If this value is null, then no {@link HIDInterface} will be set to {@link RobotFactory}.
		 * <p>
		 * The default value is {@link EmptyHIDInterface}.
		 */
		public HIDInterface hidImpl = new EmptyHIDInterface();
		/**
		 * Contains the {@link ModeSelector} to be used by the robot for choosing operation modes.
		 * If this value is null, then the operation mode of the robot will always be 
		 * {@link ModeSelector#MODE_DISABLED}.
		 * <p>
		 * The default value is `null`.
		 */
		public ModeSelector modeSelector;
		/**
		 * Contains initialization data for Flashboard in the form of {@link FlashboardInitData}.
		 * If this value is `null`, Flashboard control will not be initialized.
		 * <p>
		 * The default value is an instance of {@link FlashboardInitData}.
		 */
		public FlashboardInitData flashboardInitData = new FlashboardInitData();
		
		/**
		 * Indicates whether or not to add an auto HID update task to the {@link Scheduler}. This will
		 * refresh HID data automatically, allowing for HID-activated actions.
		 * <p>
		 * The default value is `false`.
		 */
		public boolean autoUpdateHid = false;
		
		public void copy(RobotInitializer initializer){
			super.copy(initializer);
			hidImpl = initializer.hidImpl;
			modeSelector = initializer.modeSelector;
			flashboardInitData = initializer.flashboardInitData;
			autoUpdateHid = initializer.autoUpdateHid;
		}
	}
	
	protected static final Log log = FlashUtil.getLog();
	private static RobotBase userImplement;
	private static boolean halInitialized = false;
	
	private Communications communications;
	private LocalShell shell = new LocalShell();
	
	
	//--------------------------------------------------------------------
	//----------------------------MAIN------------------------------------
	//--------------------------------------------------------------------

	public static void main(String[] args){
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		Runtime.getRuntime().addShutdownHook(new Thread(()->onShutdown()));
		
		userImplement = loadUserClass();
		if(userImplement == null){
			log.reportError("Failed to initialize user robot implementation");
			shutdown(1);
		}
		
		try{
			setupRobot();
		}catch(Throwable t){
			log.reportError("Exception occurred in robot setup!!\n"+t.getMessage());
			shutdown(1);
		}
		
		log.log("Starting robot", "RobotBase");
		try{
			userImplement.robotMain();
		}catch(Throwable t){
			log.reportError("Exception occurred in robot thread!!\n"+t.getMessage());
			shutdown(1);
		}
	}
	private static RobotBase loadUserClass(){
		String robotName = null;
		Enumeration<URL> resources = null;
	    try {
	      resources = RobotBase.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
	    } catch (IOException ex) {
	      ex.printStackTrace();
	    }
	    while (resources != null && resources.hasMoreElements()) {
	      try {
	        Manifest manifest = new Manifest(resources.nextElement().openStream());
	        robotName = manifest.getMainAttributes().getValue("Robot-Class");
	      } catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }
		log.log("User class found: "+robotName, "RobotBase");
		
		try {
			return (RobotBase) Class.forName(robotName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static void setupRobot() throws Exception{
		BasicInitializer initializer = new BasicInitializer();
		userImplement.configInit(initializer);
		
		if(initializer.initHAL){
			int halmode = initializer.halInitMode;
			log.log("Initializing HAL: "+halmode, "RobotBase");
			int status = HAL.initializeHAL(halmode);
			if(status != 0){
				throw new Exception("Failed to initialize HAL: "+status);
			}
			log.log("HAL initialized: "+HAL.boardName(), "RobotBase");
			halInitialized = true;
		}
		
		if(initializer.initCommunications){
			if(initializer.commInterface != null){
				log.log("Initializing robot communications", "RobotBase");
				userImplement.communications = new Communications("Robot", initializer.commInterface);
				log.log("Done", "RobotBase");
			}else{
				log.reportWarning("CommInterface for robot communications is null");
			}
		}
	}
	private static void onShutdown(){
		log.logTime("Shuting down...");
		if(userImplement != null){
			log.log("User shutdown...", "RobotBase");
			try {
				userImplement.robotShutdown();
			} catch (Throwable e) {
				log.reportError("Exception occurred during user shutdown!!\n"+e.getMessage());
			}
		}
		
		if(userImplement.communications != null){
			log.log("Shutting down robot communications...", "RobotBase");
			userImplement.communications.close();
			log.log("Done", "RobotBase");
		}
		
		if(halInitialized){
			log.log("Shutting down HAL...", "RobotBase");
			HAL.shutdown();
			log.log("Done", "RobotBase");
			halInitialized = false;
		}
		
		log.logTime("Shutdown successful", "RobotBase");
		log.close();
	}
	
	public static void shutdown(int code){
		System.exit(code);
	}
	public static void shutdown(){
		shutdown(0);
	}
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------
	
	public Shell getShell(){
		return shell;
	}
	public Communications getCommunications(){
		return communications;
	}
	
	protected void configInit(BasicInitializer initializer){}
	protected abstract void robotMain();
	protected abstract void robotShutdown();
}
