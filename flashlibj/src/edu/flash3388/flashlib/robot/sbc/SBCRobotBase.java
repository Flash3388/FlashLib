package edu.flash3388.flashlib.robot.sbc;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public abstract class SBCRobotBase{
	
	protected static class RobotInitializer{
		public CommInterface commInterface;
		public boolean initCommunications = true;
		
		public StateSelector stateSelector;
		private boolean useStateSelector = true;
		
		public boolean logEnabled = true;
		
		public boolean initControlStation = true;
		
		public boolean initFlashboard = true;
		public final FlashboardInitData flashboardInitData = new FlashboardInitData();
		
		public Robot robot;
	}
	
	protected static final int DEFAULT_COMM_INTERFACE_LOCAL_PORT = 5809;
	
	private static SBCRobotBase userImplement;
	private static Robot robot;
	private static Communications communications;
	private static LocalShell shell;
	private static StateSelector stateSelector;
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
		shell = new LocalShell();
		log.log("Done", "RobotBase");
		
		log.log("Loading user class...", "RobotBase");
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
	private static SBCRobotBase loadUserClass(){
		String robotName = null;
		Enumeration<URL> resources = null;
	    try {
	      resources = SBCRobotBase.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
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
			return (SBCRobotBase) Class.forName(robotName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	private static CommInterface setupDefaultCommInterface() throws IOException{
		return new TcpCommInterface(DEFAULT_COMM_INTERFACE_LOCAL_PORT);
	}
	private static void setupRobot(){
		RobotInitializer initializer = new RobotInitializer();
		userImplement.preInit(initializer);
		
		if(!initializer.logEnabled){
			log.delete();
			log.disable();
		}
		if(initializer.initControlStation){
			ControlStation.init();
		}
		
		if(initializer.initCommunications){
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
			communications.attach(shell);
			if(initializer.initControlStation)
				communications.attach(ControlStation.getInstance().getSendable());
			log.log("Done", "RobotBase");
		}
		
		if(initializer.robot == null){
			log.reportWarning("User did not provide a state selector, using default");
			initializer.robot = new SBCRobot();
		}
		robot = initializer.robot;
		
		log.log("Initializing FlashLib...");
		FlashRobotUtil.initFlashLib(robot, initializer.initFlashboard? initializer.flashboardInitData : null);
		
		log.log("Initialization Done", "RobotBase");
		log.save();
		
		if(initializer.useStateSelector){
			if (initializer.stateSelector == null) {
				log.reportWarning("User did not provide a state selector, using default");
				if(!initializer.initControlStation){
					log.reportWarning("Control Station was not enabled, using manual selector");
					initializer.stateSelector = new ManualStateSelector();
				}else
					initializer.stateSelector = new ControlStation.CsStateSelector(ControlStation.getInstance());
			}
			stateSelector = initializer.stateSelector;
		}
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
		//robot.scheduler().setDisabled(true);
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
	
	public static Communications communications(){
		return communications;
	}
	public static LocalShell shell(){
		return shell;
	}
	public static Robot robot(){
		return robot;
	}
	public static StateSelector stateSelector(){
		return stateSelector;
	}
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------
	
	protected abstract void preInit(RobotInitializer initializer);
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
