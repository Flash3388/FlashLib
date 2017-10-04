package edu.flash3388.flashlib.flashboard;

import java.io.IOException;
import java.net.InetAddress;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraView;
import edu.flash3388.flashlib.communications.CameraServer;
import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.TCPCommInterface;
import edu.flash3388.flashlib.communications.UDPCommInterface;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.BooleanSource;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.StringProperty;
import edu.flash3388.flashlib.util.beans.StringSource;
import edu.flash3388.flashlib.vision.RemoteVision;
import edu.flash3388.flashlib.vision.Vision;

/**
 * Control class for the Flashboard. Can be used to attach controls to the Flashboard, cameras, control
 * vision, etc.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class Flashboard {
	
	private Flashboard(){}
	
	public static class FlashboardInitData{
		public int initMode = INIT_FULL;
		
		public int camPort = CAMERA_PORT_ROBOT;
		public int commPort = PORT_ROBOT;
		
		public boolean tcp = true;
		public byte[] ipAddress = null;
		
		public void enableCameraServer(boolean enable){
			if(enable)
				initMode |= INIT_CAM;
			else
				initMode &= ~(INIT_CAM);
		}
		public void setCameraServerPort(int port){
			camPort = port;
		}
		
		public void enableCommunications(boolean enable){
			if(enable)
				initMode |= INIT_COMM;
			else
				initMode &= ~(INIT_COMM);
		}
		public void setCommunicationsPort(int port){
			commPort = port;
		}
		
		public void setLocalIPAddress(byte[] ipaddr){
			this.ipAddress = ipaddr;
		}
		
		public void setProtocolTCP(){
			tcp = true;
		}
		public void setProtocolUDP(){
			tcp = false;
		}
	}
	
	/**
	 * Indicates the initialization to initialize only the flashboard camera server.
	 */
	public static final byte INIT_CAM = 0x1;
	/**
	 * Indicates the initialization to initialize only the flashboard communications server.
	 */
	public static final byte INIT_COMM = 0x1 << 1;
	/**
	 * Indicates the initialization to both the flashboard camera server and communications server.
	 * This is the default initialization value.
	 */
	public static final byte INIT_FULL = INIT_CAM | INIT_COMM;
	
	/**
	 * Default communications port for Flashboard from the robot.
	 */
	public static final int PORT_ROBOT = 5800;
	/**
	 * Default communications port for Flashboard from the software.
	 */
	public static final int PORT_BOARD = 5800;
	/**
	 * Default camera communications port for Flashboard from the robot.
	 */
	public static final int CAMERA_PORT_ROBOT = 5802;
	/**
	 * Default camera communications port for Flashboard from the software.
	 */
	public static final int CAMERA_PORT_BOARD = 5802;
	
	private static boolean instance = false;
	private static int initMode = INIT_FULL;
	
	private static CameraView camViewer;
	private static CameraServer camServer;
	private static Vision vision;
	private static Communications communications;
	
	private static void checkInit(){
		if(!instance || (initMode & INIT_COMM) == 0)
			throw new IllegalStateException("Flashboard was not initialized");
	}
	
	/**
	 * Attaches a new control sendable to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @param sendable control to attach
	 * 
	 * @see Communications#attach(Sendable)
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static void attach(Sendable sendable){
		checkInit();
		communications.attach(sendable);
	}
	/**
	 * Attaches new control sendables to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @param sendables controls to attach
	 * 
	 * @see Communications#attach(Sendable...)
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static void attach(Sendable... sendables){
		checkInit();
		for (Sendable sendable : sendables) 
			communications.attach(sendable);
	}
	/**
	 * Detaches control sendable from the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @param sendable control to detach
	 * 
	 * @return true if the control was successfully detached, false otherwise
	 * 
	 * @see Communications#detach(Sendable)
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static boolean detach(Sendable sendable){
		checkInit();
		return communications.detach(sendable);
	}
	/**
	 * Detaches control sendable from the Flashboard by its id. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @param id id of the control to detach
	 * 
	 * @return true if the control was successfully detached, false otherwise
	 * 
	 * @see Communications#detach(int)
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static boolean detach(int id){
		checkInit();
		return communications.detach(id);
	}
	/**
	 * Gets a control from the Flashboard by its ID. Flashboard should be initialized first for it 
	 * to work.
	 * @param id the id of the control
	 * @return the sendable object with the given id, null if not found.
	 * @see Communications#getLocalyAttachedByID(int)
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static Sendable getLocalByID(int id){
		checkInit();
		return communications.getLocalyAttachedByID(id);
	}
	
	/**
	 * Gets whether or not this controller is connected to the remote Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @return true if connected, false otherwise
	 * 
	 * @see Communications#isConnected()
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static boolean isConnected(){
		checkInit();
		return communications.isConnected();
	}
	/**
	 * Starts the communications thread. Flashboard should be initialized first for it 
	 * to work.
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 * @see Communications#start()
	 */
	public static void start(){
		checkInit();
		communications.start();
	}
	
	/**
	 * Gets the camera view to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the camera view on the flashboard. Null if not initialized.
	 */
	public static CameraView getCameraView(){
		if(!instance)
			return null;
		return camViewer;
	}
	/**
	 * Gets the remote vision control to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the vision control to the flashboard. Null if not initialized. 
	 */
	public static Vision getVision(){
		if(!instance)
			return null;
		return vision;
	}
	/**
	 * Gets the camera server to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the camera server to the flashboard. Null if not initialized. 
	 */
	public static CameraServer getCameraServer(){
		if(!instance)
			return null;
		return camServer;
	}
	
	/**
	 * Initializes Flashboard control with parameters set in {@link FlashboardInitData} and passes them to
	 * {@link #init(int, byte[], int, int, boolean)}.
	 * 
	 * @param initData initialization data
	 * 
	 * @throws IllegalStateException if flashboard was initialized
	 */
	public static void init(FlashboardInitData initData){
		init(initData.initMode, initData.ipAddress, initData.commPort, initData.camPort, initData.tcp);
	}
	/**
	 * Intializes Flashboard control for a given mode. Uses given parameters for ports and protocol in initialization.
	 * <p>
	 * It is not recommended to use this method for initialization. You will need to make sure the Flashboard 
	 * software is set to use the same ports instead of the defaults.
	 * </p>
	 * @param mode indicates how to initialize the flashboard control.
	 * @param ipaddress the ipaddress to bind the local socket to (only for TCP), or null for default
	 * @param port standard communications port
	 * @param camport camera communications port
	 * @param tcp protocol to use: True for TCP, false for UDP.
	 * 
	 * @throws IllegalStateException if flashboard was initialized
	 */
	public static void init(int mode, byte[] ipaddress, int port, int camport, boolean tcp){
		if(instance)
			throw new IllegalStateException("Flashboard control was already initialized");
		
		try {
			if(vision == null && (initMode & INIT_COMM) != 0)
				vision = new RemoteVision("FlashboardVision");
			if(camViewer == null && (initMode & INIT_CAM) != 0)
				camViewer = new CameraView("Flashboard-CamViewer", null, new Camera[]{});
			
			if(communications == null && (initMode & INIT_COMM) != 0){
				CommInterface readi;
				if(tcp){
					if(ipaddress == null)
						readi = new TCPCommInterface(port);
					else {
						InetAddress addr = InetAddress.getByAddress(ipaddress);
						readi = new TCPCommInterface(addr, port);
					}
				}
				else readi = new UDPCommInterface(port);
				
				communications = new Communications("Flashboard", readi);
				if(vision instanceof Sendable)
					communications.attach((Sendable)vision);
			}
			
			if(camServer == null && (initMode & INIT_CAM) != 0)
				camServer = new CameraServer("Flashboard", camport, camViewer);
			
			initMode = mode;
			instance = true;
			FlashUtil.getLog().logTime("Flashboard: Initialized for mode: " + Integer.toBinaryString(initMode), "Robot");
		} catch (IOException e) {
			FlashUtil.getLog().reportError(e.getMessage());
			e.printStackTrace();
		}
	}
	/**
	 * Closes flashboard control. If the camera server was initialized, it is closed by
	 * calling {@link CameraServer#close()}. If communications was initialized, it is closed
	 * by calling {@link Communications#close()}.
	 * 
	 * @throws IllegalStateException if flashboard was not initialized
	 */
	public static void close(){
		if(!instance)
			throw new IllegalStateException("Flashboard control was not initialized");
		
		if(camServer != null)
			camServer.close();
		if(communications != null)
			communications.close();
	}
	/**
	 * Gets whether or not Flashboard was initialized. 
	 * @return true if flashboard was initialized, false otherwise.
	 */
	public static boolean flashboardInit(){
		return instance;
	}
	/**
	 * Gets the flashboard initialization mode. If flashboard was not initialized, 0 will be 
	 * returned.
	 * @return the flashboard initialization mode.
	 */
	public static int getInitMode(){
		return instance? initMode : 0;
	}
	
	
	public static FlashboardInput putInputField(String name, DoubleProperty prop){
		checkInit();
		
		FlashboardInput input = new FlashboardInput(name, prop);
		Flashboard.attach(input);
		return input;
	}
	public static FlashboardInput putInputField(String name, BooleanProperty prop){
		checkInit();
		
		FlashboardInput input = new FlashboardInput(name, prop);
		Flashboard.attach(input);
		return input;
	}
	public static FlashboardInput putInputField(String name, StringProperty prop){
		checkInit();
		
		FlashboardInput input = new FlashboardInput(name, prop);
		Flashboard.attach(input);
		return input;
	}
	public static FlashboardCheckbox putCheckBox(String name, BooleanProperty prop){
		checkInit();
		
		FlashboardCheckbox checkbox = new FlashboardCheckbox(name, prop);
		Flashboard.attach(checkbox);
		return checkbox;
	}
	public static FlashboardSlider putSlider(String name, DoubleProperty prop, double min, double max, int ticks){
		checkInit();
		
		FlashboardSlider slider = new FlashboardSlider(name, prop, min, max, ticks);
		Flashboard.attach(slider);
		return slider;
	}
	public static FlashboardButton putButton(String name, Action... actions){
		checkInit();
		
		FlashboardButton button = new FlashboardButton(name);
		for (int i = 0; i < actions.length; i++)
			button.whenPressed(actions[i]);
		Flashboard.attach(button);
		return button;
	}
	
	@SafeVarargs
	public static <T> FlashboardChooser<T> putChooser(String name, FlashboardChooser.Option<T>...options){
		checkInit();
		
		FlashboardChooser<T> chooser = new FlashboardChooser<T>(name, options);
		Flashboard.attach(chooser);
		return chooser;
	}
	
	
	public static FlashboardLabel putLabel(String name, DoubleSource prop){
		checkInit();
		
		FlashboardLabel label = new FlashboardLabel(name, prop);
		Flashboard.attach(label);
		return label;
	}
	public static FlashboardLabel putLabel(String name, BooleanSource prop){
		checkInit();
		
		FlashboardLabel label = new FlashboardLabel(name, prop);
		Flashboard.attach(label);
		return label;
	}
	public static FlashboardLabel putLabel(String name, StringSource prop){
		checkInit();
		
		FlashboardLabel label = new FlashboardLabel(name, prop);
		Flashboard.attach(label);
		return label;
	}
	
	public static FlashboardDirectionIndicator putDirectionIndicator(String name, DoubleSource prop){
		checkInit();
		
		FlashboardDirectionIndicator indicator = new FlashboardDirectionIndicator(name, prop);
		Flashboard.attach(indicator);
		return indicator;
	}
	public static FlashboardBooleanIndicator putBooleanIndicator(String name, BooleanSource prop){
		checkInit();
		
		FlashboardBooleanIndicator indicator = new FlashboardBooleanIndicator(name, prop);
		Flashboard.attach(indicator);
		return indicator;
	}
	public static FlashboardXYChart putLineChart(String name, DoubleSource xsource, DoubleSource ysource, 
			double rangeX, double minY, double maxY){
		checkInit();
		
		FlashboardXYChart chart = new FlashboardXYChart(name, FlashboardXYChart.ChartType.Line,
				xsource, ysource, rangeX, minY, maxY);
		Flashboard.attach(chart);
		return chart;
	}
	public static FlashboardXYChart putAreaChart(String name, DoubleSource xsource, DoubleSource ysource, 
			double rangeX, double minY, double maxY){
		checkInit();
		
		FlashboardXYChart chart = new FlashboardXYChart(name, FlashboardXYChart.ChartType.Area,
				xsource, ysource, rangeX, minY, maxY);
		Flashboard.attach(chart);
		return chart;
	}
	public static FlashboardBarChart putBarChart(String name, double minY, double maxY){
		checkInit();
		
		FlashboardBarChart chart = new FlashboardBarChart(name, minY, maxY);
		Flashboard.attach(chart);
		return chart;
	}
}

