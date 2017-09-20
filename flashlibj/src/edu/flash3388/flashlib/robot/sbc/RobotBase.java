package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.robot.hal.HAL;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class RobotBase implements SBC{
	
	protected static class BasicInitializer{
		public int halInitMode = 0;
		public boolean initHAL = true;
		
		public boolean initCommunications = false;
		public CommInterface commInterface = null;
		
		public void copy(BasicInitializer initializer){
			halInitMode = initializer.halInitMode;
			initHAL = initializer.initHAL;
			
			initCommunications = initializer.initCommunications;
			commInterface = initializer.commInterface;
		}
	}
	
	protected static final Log log = FlashUtil.getLog();
	private static RobotBase userImplement;
	private static boolean halInitialized = false;
	
	private Communications communications;
	protected final LocalShell shell = new LocalShell();
	
	
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
	
	public Shell shell(){
		return shell;
	}
	public Communications communications(){
		return communications;
	}
	
	protected void configInit(BasicInitializer initializer){}
	protected abstract void robotMain();
	protected abstract void robotShutdown();
}
