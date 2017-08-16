package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class SbcRobot {
	
	protected static class RobotInitializer{
		public CommInterface commInterface;
		public StateSelector stateSelector;
		
		public int flashlibInitMode = -1;
		
		public boolean logEnabled = true;
		
		public boolean initControlStation = true;
	}
	
	private static ShellExecutor executor;
	private static Communications communications;
	private static byte currentState;
	private static StateSelector stateSelector;
	private static SbcRobot userImplement;
	private static Log log;

	
	//--------------------------------------------------------------------
	//----------------------------MAIN------------------------------------
	//--------------------------------------------------------------------

	public static void main(String[] args){
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		log = FlashUtil.getLog();
		log.log("Initializing robot...", "RobotBase");
		
		log.log("Setting up shutdown hook...", "RobotBase");
		Runtime.getRuntime().addShutdownHook(new Thread(()->onShutdown()));
		log.log("Done");
		
		log.log("Initializing board...", "RobotBase");
		executor = new ShellExecutor();
		log.log("Done", "RobotBase");
		
		log.log("Loading user class...", "RobotBase");
		userImplement = loadUserClass();
		if(userImplement == null){
			log.reportError("Failed to initialize user robot");
			shutdown(1);
		}
		
		RobotInitializer initializer = new RobotInitializer();
		userImplement.preInit(initializer);
		
		if(!initializer.logEnabled){
			log.delete();
			log.setLoggingMode(Log.MODE_PRINT);
			log.log("Deleted logs", "RobotBase");
		}
		if(initializer.initControlStation){
			SbcControlStation.init();
		}
		
		log.log("Initializing FlashLib...");
		if(initializer.flashlibInitMode < 0)
			initializer.flashlibInitMode = FlashRoboUtil.FLASHBOARD_INIT | FlashRoboUtil.SCHEDULER_INIT;
		FlashRoboUtil.initFlashLib(initializer.flashlibInitMode, RobotFactory.ImplType.SBC);
		
		log.log("Initializing Communications...", "RobotBase");
		try {
			if(initializer.commInterface == null){
				log.reportWarning("User did not implement comm interface, loading default");
				initializer.commInterface = setupDefaultCommInterface();
			}
			if(initializer.commInterface == null)
				throw new Exception("Failure to initialize comm interface (null)");
		} catch (Exception e) {
			log.reportError(e.getMessage());
			shutdown(1);
		}
		communications = new Communications("Robot", initializer.commInterface);
		communications.attach(executor);
		if(initializer.initControlStation)
			communications.attach(SbcControlStation.getInstance().getSendable());
		log.log("Done", "RobotBase");
		
		log.log("Initialization Done", "RobotBase");
		log.save();
		
		log.logTime("Starting Robot");
		if (initializer.stateSelector == null) {
			log.reportWarning("User did not provide a state selector, using default");
			if(!initializer.initControlStation){
				log.reportWarning("Control Station was not enabled, using manual selector");
				initializer.stateSelector = new ManualStateSelector();
			}else
				initializer.stateSelector = new SbcControlStation.CsStateSelector(SbcControlStation.getInstance());
		}
		stateSelector = initializer.stateSelector;
		
		currentState = StateSelector.STATE_DISABLED;
		communications.start();
		
		try{
			userImplement.startRobot();
		}catch(Throwable t){
			log.reportError("Exception occurred in robot thread!!\n"+t.getMessage());
			shutdown(1);
		}
	}
	private static SbcRobot loadUserClass(){
		String robotName = null;
		Enumeration<URL> resources = null;
	    try {
	      resources = SbcRobot.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
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
		log.log("User class found: "+robotName);
		
		try {
			return (SbcRobot) Class.forName(robotName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static CommInterface setupDefaultCommInterface() throws IOException{
		return null;
	}
	private static void onShutdown(){
		log.logTime("Shuting down...");
		if(userImplement != null){
			log.log("User shutdown...");
			try {
				userImplement.stopRobot();
			} catch (Throwable e) {
				log.reportError("Exception occurred during user shutdown!!\n"+e.getMessage());
				FlashUtil.delay(5);
			}
		}
		if(RobotFactory.hasSchedulerInstance()){
			RobotFactory.disableScheduler(true);
		}
		if(communications != null){
			log.log("Closing communications...");
			communications.close();
			log.log("Done");
		}
		
		log.log("Settings saved");
		
		log.logTime("Shutdown successful");
		log.save();
		log.close();
	}
	
	public static void shutdown(int code){
		System.exit(code);
	}
	public static void shutdown(){
		shutdown(0);
	}
	
	public static byte getCurrentState(){
		if (stateSelector != null) 
			currentState = stateSelector.getState();
		else currentState = StateSelector.STATE_DISABLED;
		
		return currentState;
	}
	public static boolean isDisabled(){
		return currentState == StateSelector.STATE_DISABLED;
	}
	
	public static ShellExecutor shell(){
		return executor;
	}
	public static StateSelector stateSelector(){
		return stateSelector;
	}
	public static Communications communications(){
		return communications;
	}
	/*public static Board board(){
		return board;
	}
	public static String boardName(){
		return board.getName();
	}*/
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------
	
	protected abstract void preInit(RobotInitializer initializer);
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
