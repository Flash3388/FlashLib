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
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import io.silverspoon.bulldog.core.io.IOPort;
import io.silverspoon.bulldog.core.io.bus.i2c.I2cBus;
import io.silverspoon.bulldog.core.io.bus.spi.SpiBus;
import io.silverspoon.bulldog.core.io.serial.SerialPort;
import io.silverspoon.bulldog.core.pin.Pin;
import io.silverspoon.bulldog.core.platform.Board;
import io.silverspoon.bulldog.core.platform.Platform;

import static edu.flash3388.flashlib.robot.FlashRoboUtil.*;

public abstract class SbcBot {
	
	public static final byte STATE_DISABLED = 0x00;
	public static final byte STATE_AUTONOMOUS = 0x01;
	public static final byte STATE_TELEOP = 0x02;
	
	public static final String PROP_USER_CLASS = "user.class";
	public static final String PROP_BOARD_SHUTDOWN = "board.shutdown";
	public static final String PROP_COMM_PORT = "comm.port";
	public static final String PROP_COMM_INTERFACE = "comm.interface";
	public static final String PROP_COMM_SERIAL_PORT = "comm.serial.port";
	public static final String PROP_FLASHBOARD_INIT = "flashboard.init";
	
	private static final String PROPERTIES_FILE = "robot.xml";
	
	private static Board board;
	private static SbcControlStation controlStation;
	private static ShellExecutor executor;
	private static Communications communications;
	private static byte currentState;
	private static StateSelector stateSelector;
	private static SbcBot userImplement;
	private static Log log;

	public static void main(String[] args){
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
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
			ConstantsHandler.loadConstantsFromXml(PROPERTIES_FILE);
		else{
			try {
				file.createNewFile();
			} catch (IOException e) {}
		}
		loadDefaultSettings();
		ConstantsHandler.saveConstantsToXml(PROPERTIES_FILE);
		ConstantsHandler.printAll(log);
		
		log.log("Initializing FlashLib...");
		int initcode = SCHEDULER_INIT | 
				(ConstantsHandler.getBooleanValue(PROP_FLASHBOARD_INIT)? FLASHBOARD_INIT : 0);
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
			userClassName = ConstantsHandler.getStringValue(PROP_USER_CLASS);
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
		currentState = STATE_DISABLED;
		userImplement = userClass;
		stateSelector = userImplement.initStateSelector();
		communications.start();
		
		try{
			userClass.startRobot();
		}catch(Throwable t){
			log.reportError("Exception occurred in robot thread!!\n"+t.getMessage());
			shutdown(1);
		}
	}
	private static CommInterface setupCommInterface() throws IOException{
		int port = ConstantsHandler.getIntegerValue(PROP_COMM_PORT);
		if(port <= 0) return null;
		String interfaceType = ConstantsHandler.getStringValue(PROP_COMM_INTERFACE, "");
		if(interfaceType.equalsIgnoreCase("udp"))
			return new UdpCommInterface(port);
        else if(interfaceType.equalsIgnoreCase("tcp"))
			return new TcpCommInterface(port);
        else if(interfaceType.equalsIgnoreCase("serial")){
        	String data = ConstantsHandler.getStringValue(PROP_COMM_SERIAL_PORT);
        	if(data == null){
        		log.reportError("To initialize serial port comm, a serial port has to be defined under "+
        				PROP_COMM_SERIAL_PORT+" property");
        		return null;
        	}
        	
        	SerialPort sport = getSerialPort(data);
        	if(sport == null){
        		log.reportError("property "+ PROP_COMM_SERIAL_PORT+" contains an invalid serial port name: "+
        					data);
        		return null;
        	}
        	
        	return new SbcSerialCommInterface(sport, true);
        }
		return null;
	}
	private static void loadDefaultSettings(){
		if(!ConstantsHandler.hasBoolean(PROP_BOARD_SHUTDOWN))
			ConstantsHandler.putBoolean(PROP_BOARD_SHUTDOWN, false);
		if(!ConstantsHandler.hasNumber(PROP_COMM_PORT)){
			ConstantsHandler.putNumber(PROP_COMM_PORT, 0);
			FlashUtil.getLog().reportError("Missing Property: "+PROP_COMM_PORT);
		}
		if(!ConstantsHandler.hasString(PROP_COMM_INTERFACE))
			ConstantsHandler.putString(PROP_COMM_INTERFACE, "tcp");
		if(!ConstantsHandler.hasBoolean(PROP_FLASHBOARD_INIT))
			ConstantsHandler.putBoolean(PROP_FLASHBOARD_INIT, true);
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
		if(schedulerHasInstance()){
			disableScheduler(true);
			Scheduler.getInstance().removeAllActions();
		}
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
		
		FlashUtil.terminateExecutor();
		
		ConstantsHandler.saveConstantsToXml(PROPERTIES_FILE);
		log.log("Settings saved");
		
		log.logTime("Shutdown successful");
		boolean shutdown = ConstantsHandler.getBooleanValue(PROP_BOARD_SHUTDOWN);
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
	
	public static byte getCurrentState(){
		if (stateSelector != null) 
			currentState = stateSelector.getState();
		else currentState = STATE_DISABLED;
		
		return currentState;
	}
	public static boolean isDisabled(){
		return currentState == STATE_DISABLED;
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
	
	//--------------------------------------------------------------------
	//----------------------Implementable---------------------------------
	//--------------------------------------------------------------------
	
	protected StateSelector initStateSelector(){
		return new SbcControlStation.CsStateSelector(getControlStation());
	}
	
	protected abstract void startRobot();
	protected abstract void stopRobot();
}
