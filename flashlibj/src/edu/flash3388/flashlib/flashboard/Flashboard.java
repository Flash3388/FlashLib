package edu.flash3388.flashlib.flashboard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.flash3388.flashlib.cams.Camera;
import edu.flash3388.flashlib.cams.CameraView;
import edu.flash3388.flashlib.communications.CameraServer;
import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.BooleanSource;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.ValueSource;
import edu.flash3388.flashlib.vision.RemoteVision;

/**
 * Control class for the Flashboard. Can be used to attach controls to the Flashboard, cameras, control
 * vision, etc.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Flashboard {
	
	private Flashboard(){}
	
	public static class FlashboardInitData{
		public int initMode = INIT_FULL;
		public int camPort = CAMERA_PORT_ROBOT;
		public int commPort = PORT_ROBOT;
		public boolean tcp = true;
	}
	
	/**
	 * Indicates the initialization to initialize only the flashboard camera server.
	 * Set this value using {@link #setInitMode(int)}. It will be used when {@link #init()} is
	 * called.
	 */
	public static final byte INIT_CAM = 0x1;
	/**
	 * Indicates the initialization to initialize only the flashboard communications server.
	 * Set this value using {@link #setInitMode(int)}. It will be used when {@link #init()} is
	 * called.
	 */
	public static final byte INIT_COMM = 0x1 << 1;
	/**
	 * Indicates the initialization to both the flashboard camera server and communications server.
	 * Set this value using {@link #setInitMode(int)}. It will be used when {@link #init()} is
	 * called.
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
	private static FlashboardInitData initData = null;
	
	private static CameraView camViewer;
	private static CameraServer camServer;
	private static RemoteVision vision;
	private static Communications communications;
	private static Map<String, Sendable> sendables;
	
	private static void checkInit(){
		if(!instance || (initMode & INIT_COMM) == 0)
			throw new IllegalStateException("Flashboard was not initialized");
		if(sendables == null)
			sendables = new HashMap<String, Sendable>();
	}
	
	/**
	 * Gets the flashboard initialization data holder. It is an instance of {@link FlashboardInitData}
	 * which will be used to fill missing initialization data when initializing the Flashboard.
	 * @return the initialization data holder.
	 */
	public static FlashboardInitData getInitData(){
		if(initData == null)
			initData = new FlashboardInitData();
		return initData;
	}
	/**
	 * Sets the Flashboard to use TCP protocol for communications. 
	 * Works only if Flashboard was not initialized yet.
	 */
	public static void setProtocolTcp(){
		if(!instance)
			getInitData().tcp = true;
	}
	/**
	 * Sets the Flashboard to use UDP protocol for communications. 
	 * Works only if Flashboard was not initialized yet.
	 */
	public static void setProtocolUdp(){
		if(!instance)
			getInitData().tcp = false;
	}
	/**
	 * Sets the initialization mode to be used when {@link #init()} is called. If flashboard was initialized,
	 * this will do nothing.
	 * 
	 * @param mode initialization mode
	 */
	public static void setInitMode(int mode){
		if(!instance)
			getInitData().initMode = mode;
	}
	/**
	 * Sets the communications port used when initializing flashboard using {@link #init(int, boolean)}.
	 * If flashboard was already initialized, this will do nothing.
	 * 
	 * @param port the local communications port
	 */
	public static void setCommPort(int port){
		if(!instance)
			getInitData().commPort = port;
	}
	/**
	 * Sets the camera server port used when initializing flashboard using {@link #init(int, boolean)}.
	 * If flashboard was already initialized, this will do nothing.
	 * 
	 * @param port the local camera server port
	 */
	public static void setCamPort(int port){
		if(!instance)
			getInitData().camPort = port;
	}
	
	/**
	 * Attaches a new control sendable to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @param sendable control to attach
	 * @see Communications#attach(Sendable)
	 */
	public static void attach(Sendable sendable){
		checkInit();
		communications.attach(sendable);
	}
	/**
	 * Attaches new control sendables to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @param sendables controls to attach
	 * @see Communications#attach(Sendable...)
	 */
	public static void attach(Sendable... sendables){
		checkInit();
		for (Sendable sendable : sendables) 
			communications.attach(sendable);
	}
	/**
	 * Detaches control sendable from the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @param sendable control to detach
	 * @return true if the control was successfully detached, false otherwise
	 * @see Communications#detach(Sendable)
	 */
	public static boolean detach(Sendable sendable){
		checkInit();
		return communications.detach(sendable);
	}
	/**
	 * Detaches control sendable from the Flashboard by its id. Flashboard should be initialized first for it 
	 * to work.
	 * @param id id of the control to detach
	 * @return true if the control was successfully detached, false otherwise
	 * @see Communications#detach(int)
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
	 */
	public static Sendable getLocalByID(int id){
		checkInit();
		return communications.getLocalyAttachedByID(id);
	}
	
	/**
	 * Gets whether or not this controller is connected to the remote Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return true if connected, false otherwise
	 * @see Communications#isConnected()
	 */
	public static boolean isConnected(){
		checkInit();
		return communications.isConnected();
	}
	/**
	 * Starts the communications thread. Flashboard should be initialized first for it 
	 * to work.
	 * @see Communications#start()
	 */
	public static void start(){
		checkInit();
		communications.start();
	}
	/**
	 * Closes the communications thread and interface. Flashboard should be initialized first for it 
	 * to work.
	 * @see Communications#close()
	 */
	public static void close(){
		checkInit();
		communications.close();
	}
	
	/**
	 * Gets the camera view to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the camera view on the flashboard. Null if not initialized.
	 */
	public static CameraView getCameraView(){
		return camViewer;
	}
	/**
	 * Gets the remote vision control to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the vision control to the flashboard. Null if not initialized. 
	 */
	public static RemoteVision getVision(){
		return vision;
	}
	/**
	 * Gets the camera server to the Flashboard. Flashboard should be initialized first for it 
	 * to work.
	 * @return the camera server to the flashboard. Null if not initialized. 
	 */
	public static CameraServer getCameraServer(){
		return camServer;
	}
	
	/**
	 * Initializes Flashboard control. Uses protocol set before initialization 
	 * ({@link #setProtocolTcp()} or {@link #setProtocolUdp()}) where the default is TCP. Uses the 
	 * default ports for communications: {@link #PORT_ROBOT} and {@link #CAMERA_PORT_ROBOT}.
	 *  
	 * Uses a value set by {@link #setInitMode(int mode)} for initialization. Default is {@link #INIT_FULL}.
	 */
	public static void init(){
		init(getInitData().initMode);
	}
	/**
	 * Initializes Flashboard control. Uses protocol set before initialization 
	 * ({@link #setProtocolTcp()} or {@link #setProtocolUdp()}) where the default is TCP. Uses the 
	 * default ports for communications: {@link #PORT_ROBOT} and {@link #CAMERA_PORT_ROBOT}.
	 * 
	 * @param mode indicates how to initialize the flashboard control mode
	 */
	public static void init(int mode){
		init(mode, getInitData().tcp);
	}
	/**
	 * Initializes Flashboard control. Uses a given protocol for communications. Uses the 
	 * default ports for communications: {@link #PORT_ROBOT} and {@link #CAMERA_PORT_ROBOT}.
	 * 
	 * @param mode indicates how to initialize the flashboard control mode
	 * @param tcp protocol to use: True for TCP, false for UDP.
	 */
	public static void init(int mode, boolean tcp){
		init(mode, getInitData().commPort, getInitData().camPort, tcp);
	}
	/**
	 * Intializes Flashboard control for a given mode. Uses given parameters for ports and protocol in initialization.
	 * <p>
	 * It is not recommended to use this method for initialization. You will need to make sure the Flashboard 
	 * software is set to use the same ports instead of the defaults.
	 * </p>
	 * @param mode indicates how to initialize the flashboard control.
	 * @param port standard communications port
	 * @param camport camera communications port
	 * @param tcp protocol to use: True for TCP, false for UDP.
	 */
	public static void init(int mode, int port, int camport, boolean tcp){
		if(!instance){
			try {
				if(vision == null && (initMode & INIT_COMM) != 0)
					vision = new RemoteVision();
				if(camViewer == null && (initMode & INIT_CAM) != 0)
					camViewer = new CameraView("Flashboard-CamViewer", null, new Camera[]{});
				
				if(communications == null && (initMode & INIT_COMM) != 0){
					CommInterface readi;
					if(tcp)
						readi = new TcpCommInterface(port);
					else readi = new UdpCommInterface(port);
					
					communications = new Communications("Flashboard", readi);
					communications.attach(vision);
				}
				
				if(camServer == null && (initMode & INIT_CAM) != 0)
					camServer = new CameraServer("Flashboard", camport, camViewer);
				
				initMode = mode;
				instance = true;
				FlashUtil.getLog().logTime("Flashboard: Initialized for mode: " + Integer.toBinaryString(initMode));
			} catch (IOException e) {
				FlashUtil.getLog().reportError(e.getMessage());
			}
		}
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
	
	
	public static DashboardNumberInput putInputField(String name, DoubleProperty prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardNumberInput))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardNumberInput) sen;
		}
		DashboardNumberInput input = new DashboardNumberInput(name, prop);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	public static DashboardBooleanInput putInputField(String name, BooleanProperty prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardBooleanInput))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardBooleanInput) sen;
		}
		DashboardBooleanInput input = new DashboardBooleanInput(name, prop);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	public static DashboardStringInput putInputField(String name, Property<String> prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardStringInput))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardStringInput) sen;
		}
		DashboardStringInput input = new DashboardStringInput(name, prop, InputType.String);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	
	public static DashboardSlider putSlider(String name, DoubleProperty prop, double min, double max, int ticks){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardSlider))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardSlider) sen;
		}
		DashboardSlider input = new DashboardSlider(name, prop, min, max, ticks);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	public static DashboardButton putButton(String name, Action... actions){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardButton))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardButton) sen;
		}
		DashboardButton input = new DashboardButton(name);
		for (int i = 0; i < actions.length; i++)
			input.whenPressed(actions[i]);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	
	@SuppressWarnings("unchecked")
	@SafeVarargs
	public static <T> DashboardChooser<T> putChooser(String name, DashboardChooser.Option<T>...options){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardChooser))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardChooser<T>) sen;
		}
		DashboardChooser<T> input = new DashboardChooser<T>(name, options);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	
	
	public static DashboardDoubleProperty putData(String name, DoubleSource prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardDoubleProperty))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardDoubleProperty) sen;
		}
		DashboardDoubleProperty input = new DashboardDoubleProperty(name, prop);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	public static DashboardBooleanProperty putData(String name, BooleanSource prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardBooleanProperty))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardBooleanProperty) sen;
		}
		DashboardBooleanProperty input = new DashboardBooleanProperty(name, prop);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
	public static DashboardStringProperty putData(String name, ValueSource<String> prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardStringProperty))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardStringProperty) sen;
		}
		DashboardStringProperty input = new DashboardStringProperty(name, prop);
		sendables.put(name, input);
		Flashboard.attach(input);
		return input;
	}
}

