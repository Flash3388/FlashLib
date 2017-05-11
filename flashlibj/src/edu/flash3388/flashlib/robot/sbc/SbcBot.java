package edu.flash3388.flashlib.robot.sbc;

import static edu.flash3388.flashlib.util.FlashUtil.*;
import static edu.flash3388.flashlib.robot.Scheduler.*;

import java.io.File;
import java.io.IOException;

import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.util.Properties;
import io.silverspoon.bulldog.core.io.IOPort;
import io.silverspoon.bulldog.core.io.bus.i2c.I2cBus;
import io.silverspoon.bulldog.core.io.bus.spi.SpiBus;
import io.silverspoon.bulldog.core.io.serial.SerialPort;
import io.silverspoon.bulldog.core.pin.Pin;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;

import static edu.flash3388.flashlib.robot.FlashRoboUtil.*;

public abstract class SbcBot {
	
	public static class SbcState{
		
		public final byte value;
		protected SbcState(byte value){
			this.value = value;
		}
		
		public static final byte DISABLED = 0x00;
		public static final byte TELEOP = 0x01;
		public static final byte AUTONOMOUS = 0x02;
		
		public static final SbcState Disabled = new SbcState(DISABLED);
		public static final SbcState Teleop = new SbcState(TELEOP);
		public static final SbcState Autonomous = new SbcState(AUTONOMOUS);
		
		public static SbcState byValue(byte val){
			switch (val) {
				case DISABLED: return SbcState.Disabled;
				case TELEOP: return SbcState.Teleop;
				case AUTONOMOUS: return SbcState.Autonomous;
			}
			return null;
		}
	}
	
	public static final String PROP_USER_CLASS = "user.class";
	public static final String PROP_BOARD_SHUTDOWN = "board.shutdown";
	public static final String PROP_COMM_PORT = "comm.port";
	public static final String PROP_COMM_INTERFACE = "comm.interface";
	public static final String PROP_FLASHBOARD_INIT = "flashboard.init";
	
	private static final String NATIVE_LIBRARY_NAME = "";
	private static final String PROPERTIES_FILE = "robot.ini";
	
	private static Board board;
	private static SbcControlStation controlStation;
	private static ShellExecutor executor;
	private static Communications communications;
	private static SbcState currentState;
	private static StateSelector stateSelector;
	private static SbcBot userImplement;
	private static Properties properties = new Properties();
	private static Log log;

	public static void main(String[] args){
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		if(!NATIVE_LIBRARY_NAME.equals(""))
			System.loadLibrary(NATIVE_LIBRARY_NAME);
		
		setStart();
		log = FlashUtil.getLog();
		log.logTime("Initializing robot...");
		
		log.log("Setting up shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread(()->onShutdown()));
		log.log("Done");
		
		log.log("Initializing board...");
		board = Platform.createBoard();
		executor = new ShellExecutor();
		log.log("Done :: board-name="+getBoardName());
		
		log.log("Loading settings...");
		File file = new File(PROPERTIES_FILE);
		if(file.exists())
			properties.loadFromFile(PROPERTIES_FILE);
		else{
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		loadDefaultSettings();
		loadSettings(args);
		properties.saveToFile(PROPERTIES_FILE);
		printSettings();
		
		log.log("Initializing FlashLib...");
		int initcode = SCHEDULER_INIT | 
				(getProperties().getBooleanProperty(PROP_FLASHBOARD_INIT)? FLASHBOARD_INIT : 0);
		initFlashLib(initcode, RobotFactory.ImplType.SBC);
		
		log.log("Initializing Communications...");
		CommInterface inter = null;
		try {
			inter = setupCommInterface();
			if(inter == null)
				throw new Exception("Failure to initialize comm interface (null)");
		} catch (Exception e) {
			log.reportError(e.getMessage());
			shutdown(1);
		}
		controlStation = new SbcControlStation();
		communications = new Communications("Robot", inter);
		communications.attach(executor);
		communications.attach(controlStation);
		log.log("Done");
		
		log.logTime("Initialization Done");
		log.save();
		
		log.log("Loading user class...");
		SbcBot userClass = null;
		String userClassName = "";
		try {
			userClassName = properties.getProperty(PROP_USER_CLASS);
			if(userClassName == null || userClassName.equals(""))
				throw new ClassNotFoundException("User class missing! Must be set to "+PROP_USER_CLASS+" property");
			userClass = (SbcBot) Class.forName(userClassName).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			log.reportError(e.getMessage());
			shutdown(1);
		}
		log.log("User class instantiated: "+userClassName);
		
		log.logTime("Starting Robot");
		if(Flashboard.flashboardInit())
			FlashRoboUtil.startFlashboard();
		currentState = SbcState.Disabled;
		stateSelector = new SbcControlStation.CsStateSelector(controlStation);
		userImplement = userClass;
		communications.start();
		userClass.startRobot();
	}
	private static CommInterface setupCommInterface() throws IOException{
		int port = properties.getIntegerProperty(PROP_COMM_PORT);
		if(port <= 0) return null;
		String interfaceType = properties.getProperty(PROP_COMM_INTERFACE);
		if(interfaceType.equalsIgnoreCase("udp"))
			return new UdpCommInterface(port);
        else if(interfaceType.equalsIgnoreCase("tcp"))
			return new TcpCommInterface(port);
        else if(interfaceType.startsWith("serial")){
        	
        }
		return null;
	}
	private static void loadSettings(String[] args){
		for (int i = 0; i < args.length; i++) {
			String[] splits = args[i].split(":");
			if(splits.length == 2)
				properties.putProperty(splits[0], splits[1]);
		}
	}
	private static void loadDefaultSettings(){
		if(properties.getProperty(PROP_BOARD_SHUTDOWN) == null)
			properties.putBooleanProperty(PROP_BOARD_SHUTDOWN, false);
		if(properties.getProperty(PROP_COMM_PORT) == null){
			properties.putIntegerProperty(PROP_COMM_PORT, 0);
			FlashUtil.getLog().reportError("Missing Property: "+PROP_COMM_PORT);
		}
		if(properties.getProperty(PROP_COMM_INTERFACE) == null)
			properties.putProperty(PROP_COMM_INTERFACE, "udp");
		if(properties.getProperty(PROP_FLASHBOARD_INIT) == null)
			properties.putBooleanProperty(PROP_FLASHBOARD_INIT, true);
	}
	private static void onShutdown(){
		log.logTime("Shuting down...");
		if(userImplement != null){
			log.log("User shutdown...");
			userImplement.stopRobot();
		}
		if(schedulerHasInstance())
			disableScheduler(true);
		if(communications != null){
			log.log("Closing communications...");
			communications.close();
			log.log("Done");
		}
		if(board != null){
			log.log("Shutting down board...");
			board.shutdown();
			log.log("Done");
		}
		properties.saveToFile(PROPERTIES_FILE);
		log.log("Settings saved");
		
		log.logTime("Shutdown successful");
		boolean shutdown = properties.getBooleanProperty(PROPERTIES_FILE);
		log.log("Board shutdown="+shutdown);
		log.save();
		log.close();
		if(shutdown){
			try {
				Runtime.getRuntime().exec("shutdown -s -t 0");
			} catch (IOException e) {}
		}
	}
	
	public static void shutdown(int code){
		System.exit(code);
	}
	public static void shutdown(){
		shutdown(0);
	}
	public static void printSettings(){
		String[] keys = properties.keys(),
				 values = properties.values();
		String print = "Settings:\n";
		for (int i = 0; i < values.length; i++) 
			print += "\t"+keys[i]+"="+values[i]+"\n";
		log.log(print);
	}
	public static void setProperty(String property, String value){
		properties.putProperty(property, value);
	}
	public static String getProperty(String property){
		return properties.getProperty(property);
	}
	public static Properties getProperties(){
		return properties;
	}
	
	public static SbcState getCurrentState(){
		if (stateSelector != null) {
			SbcState nState = stateSelector.getState();
			currentState = nState != null? nState : SbcState.Disabled;
		}
		return currentState;
	}
	public static boolean isDisabled(){
		return currentState.value == SbcState.DISABLED;
	}
	
	public static ShellExecutor getShell(){
		return executor;
	}
	public static Communications getCommunications(){
		return communications;
	}
	public static Board getBoard(){
		return board;
	}
	public static SbcControlStation getControlStation(){
		return controlStation;
	}
	public static String getBoardName(){
		return board.getName();
	}
	public static String getBoardProperty(String propertyName){
		return board.getProperty(propertyName);
	}
	public static SerialPort getSerialPort(String name){
		return board.getSerialPort(name);
	}
	public static I2cBus getI2cBus(String name){
		return board.getI2cBus(name);
	}
	public static SpiBus getSpiBus(String name){
		return board.getSpiBus(name);
	}
	public static Pin getPin(String name){
		return board.getPin(name);
	}
	public static IOPort getIOPort(String name){
		return board.getIOPortByName(name);
	}
	
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
