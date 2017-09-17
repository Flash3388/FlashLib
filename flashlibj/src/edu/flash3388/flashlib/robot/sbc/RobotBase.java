package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.hal.HAL;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class RobotBase{
	
	private static RobotBase userImplement;
	
	private static boolean halInitialized = false;
	
	protected static final LocalShell shell = new LocalShell();
	protected static final Log log = FlashUtil.getLog();
	
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
			userImplement.startRobot();
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
		int halmode = userImplement.getHALMode();
		if(halmode > 0){
			log.log("Initializing HAL: "+halmode, "RobotBase");
			int status = HAL.initializeHAL(halmode);
			if(status != 0){
				throw new Exception("Failed to initialize HAL: "+status);
			}
			log.log("HAL initialized: "+HAL.boardName(), "RobotBase");
			halInitialized = true;
		}
	}
	private static void onShutdown(){
		log.logTime("Shuting down...");
		if(userImplement != null){
			log.log("User shutdown...", "RobotBase");
			try {
				userImplement.stopRobot();
			} catch (Throwable e) {
				log.reportError("Exception occurred during user shutdown!!\n"+e.getMessage());
			}
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
	
	/**
	 * Gets the initialization mode to use for the Hardware Abstraction Layer when {@link HAL#initializeHAL(int)} is
	 * called. If the value is negative, the initialization is skipped.
	 * <p>
	 * By default, this method returns 0. Users can override this method and return the wanted value.
	 * 
	 * @return the init mode for HAL, or a negative value to not initialize
	 */
	protected int getHALMode(){
		return 0;
	}
	
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
