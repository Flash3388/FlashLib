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
	private static Map<String, Sendable> sendables;
	
	private static void checkInit(){
		if(!instance || (initMode & INIT_COMM) == 0)
			throw new IllegalStateException("Flashboard was not initialized");
		if(sendables == null)
			sendables = new HashMap<String, Sendable>();
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
	 * {@link #init(int, int, int, boolean)}.
	 * 
	 * @param initData initialization data
	 */
	public static void init(FlashboardInitData initData){
		init(initData.initMode, initData.commPort, initData.camPort, initData.tcp);
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
		if(instance)
			throw new IllegalStateException("Flashboard control was already initialized");
		
		try {
			if(vision == null && (initMode & INIT_COMM) != 0)
				vision = new RemoteVision("FlashboardVision");
			if(camViewer == null && (initMode & INIT_CAM) != 0)
				camViewer = new CameraView("Flashboard-CamViewer", null, new Camera[]{});
			
			if(communications == null && (initMode & INIT_COMM) != 0){
				CommInterface readi;
				if(tcp)
					readi = new TCPCommInterface(port);
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
	
	
	public static DashboardDoubleInput putInputField(String name, DoubleProperty prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardDoubleInput))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardDoubleInput) sen;
		}
		DashboardDoubleInput input = new DashboardDoubleInput(name, prop);
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
	public static DashboardStringInput putInputField(String name, StringProperty prop){
		checkInit();
		Sendable sen = sendables.get(name);
		if(sen != null){
			if(!(sen instanceof DashboardStringInput))
				throw new IllegalArgumentException("The name is already used for a different sendable");
			return (DashboardStringInput) sen;
		}
		DashboardStringInput input = new DashboardStringInput(name, prop);
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
	public static DashboardStringProperty putData(String name, StringSource prop){
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

