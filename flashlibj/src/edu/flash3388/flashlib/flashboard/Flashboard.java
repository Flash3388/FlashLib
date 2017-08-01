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
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.Property;
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
	
	/**
	 * Default communications port for Flashboard from the robot.
	 */
	public static final int PORT_ROBOT = 5801;
	/**
	 * Default communications port for Flashboard from the software.
	 */
	public static final int PORT_BOARD = 5800;
	/**
	 * Default camera communications port for Flashboard from the robot.
	 */
	public static final int CAMERA_PORT_ROBOT = 5803;
	/**
	 * Default camera communications port for Flashboard from the software.
	 */
	public static final int CAMERA_PORT_BOARD = 5802;
	
	private static boolean instance = false, tcp = true;
	private static CameraView camViewer;
	private static CameraServer camServer;
	private static RemoteVision vision;
	private static Communications communications;
	private static Map<String, Sendable> sendables;
	
	private static void checkInit(){
		if(!instance)
			throw new IllegalStateException("Flashboard was not initialized");
		if(sendables == null)
			sendables = new HashMap<String, Sendable>();
	}
	
	/**
	 * Sets the Flashboard to use TCP protocol for communications. 
	 * Works only if Flashboard was not initialized yet.
	 */
	public static void setProtocolTcp(){
		tcp = true;
	}
	/**
	 * Sets the Flashboard to use UDP protocol for communications. 
	 * Works only if Flashboard was not initialized yet.
	 */
	public static void setProtocolUdp(){
		tcp = false;
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
	 */
	public static void init(){
		init(tcp);
	}
	/**
	 * Initializes Flashboard control. Uses a given protocol for communications. Uses the 
	 * default ports for communications: {@link #PORT_ROBOT} and {@link #CAMERA_PORT_ROBOT}.
	 * 
	 * @param tcp protocol to use: True for TCP, false for UDP.
	 */
	public static void init(boolean tcp){
		init(PORT_ROBOT, CAMERA_PORT_ROBOT, tcp);
	}
	/**
	 * Intializes Flashboard control. Uses given parameters for ports and protocol in initialization.
	 * <p>
	 * It is not recommended to use this method for initialization. You will need to make sure the Flashboard 
	 * software is set to use the same ports instead of the defaults.
	 * </p>
	 * @param port standard communications port
	 * @param camport camera communications port
	 * @param tcp protocol to use: True for TCP, false for UDP.
	 */
	public static void init(int port, int camport, boolean tcp){
		if(!instance){
			Flashboard.tcp = tcp;
			try {
				if(vision == null)
					vision = new RemoteVision();
				if(camViewer == null)
					camViewer = new CameraView("Flashboard-CamViewer", null, new Camera[]{});
				
				if(communications == null){
					CommInterface readi;
					if(tcp)
						readi = new TcpCommInterface(port);
					else readi = new UdpCommInterface(port);
					
					communications = new Communications("Flashboard", readi);
					communications.attach(vision);
				}
				
				if(camServer == null)
					camServer = new CameraServer("Flashboard", camport, camViewer);
				
				instance = true;
				FlashUtil.getLog().logTime("Flashboard: Initialized at port " + port);
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
	
	
	public static DashboardDoubleProperty putData(String name, DoubleProperty prop){
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
	public static DashboardBooleanProperty putData(String name, BooleanProperty prop){
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
	public static DashboardStringProperty putData(String name, Property<String> prop){
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

