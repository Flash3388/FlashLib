package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class RobotBase{
	
	private static RobotBase userImplement;
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
			log.reportError("Failed to initialize user robot");
			shutdown(1);
		}
		
		setupRobot();
		
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
	private static void setupRobot(){
		//CURRENTLY EMPTY
	}
	private static void onShutdown(){
		log.logTime("Shuting down...");
		if(userImplement != null){
			log.log("User shutdown...");
			try {
				userImplement.stopRobot();
			} catch (Throwable e) {
				log.reportError("Exception occurred during user shutdown!!\n"+e.getMessage());
			}
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
	
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
