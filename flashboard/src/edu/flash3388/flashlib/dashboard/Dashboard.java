package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.opencv.core.Core;

import edu.flash3388.flashlib.dashboard.controls.BarChartControl;
import edu.flash3388.flashlib.dashboard.controls.CameraViewer;
import edu.flash3388.flashlib.dashboard.controls.EmergencyStopControl;
import edu.flash3388.flashlib.dashboard.controls.HIDControl;
import edu.flash3388.flashlib.dashboard.controls.ModeSelectorControl;
import edu.flash3388.flashlib.dashboard.controls.TesterControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import edu.flash3388.flashlib.io.XMLObjectInputStream;
import edu.flash3388.flashlib.io.XMLObjectOutputStream;
import edu.flash3388.flashlib.communications.CameraClient;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.IPCommInterface;
import edu.flash3388.flashlib.communications.TCPCommInterface;
import edu.flash3388.flashlib.communications.UDPCommInterface;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanSource;
import edu.flash3388.flashlib.util.beans.PropertyHandler;
import edu.flash3388.flashlib.vision.ThreadedVisionRunner;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.vision.VisionProcessing;
import edu.flash3388.flashlib.vision.VisionRunner;
import edu.flash3388.flashlib.vision.cv.CvSource;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Dashboard extends Application {

	//--------------------------------------------------------------------
	//-----------------------Classes--------------------------------------
	//--------------------------------------------------------------------
	
	public static class Updater{
		private static final int EXECUTOR_THREAD_POOL = 5;
		private static final int EXECUTION_INTERVAL = 5; //ms
		private ScheduledExecutorService executor;
		
		private Map<Runnable, ScheduledFuture<?>> futuresMap;
		
		public Updater() {
			executor = Executors.newScheduledThreadPool(EXECUTOR_THREAD_POOL);
			futuresMap = new HashMap<Runnable, ScheduledFuture<?>>();
		}
		
		public void addTask(Runnable runnable){
			 ScheduledFuture<?> future = executor.scheduleWithFixedDelay(runnable, EXECUTION_INTERVAL, EXECUTION_INTERVAL, 
					TimeUnit.MILLISECONDS);
			 futuresMap.put(runnable, future);
		}
		public void execute(Runnable runnable){
			executor.execute(runnable);
		}
		public boolean removeTask(Runnable runnable) {
			ScheduledFuture<?> future = futuresMap.get(runnable);
			if (future == null)
				return false;
			
			futuresMap.remove(runnable);
			return future.cancel(true);
		}
		
		void shutdown() {
			executor.shutdown();
		}
		boolean isTerminated() {
			return executor.isTerminated();
		}
		void forceShutdown() throws InterruptedException{
			executor.shutdownNow();
			
			while (!executor.isTerminated()) {
				executor.awaitTermination(EXECUTION_INTERVAL, TimeUnit.MILLISECONDS);
			}
		}
	}
	
	private static class HostRetrieverTask implements Runnable{

		private boolean stop = false;
		
		private boolean retreiveComm = false;
		private boolean retreiveCam = false;
		private BooleanSource waitSource;
		
		private InetAddress commAddress;
		private InetAddress camAddress;
		
		void resetComm(){
			if(stop)
				return;
			commAddress = null;
			retreiveComm = true;
		}
		void resetCam(){
			if(stop)
				return;
			camAddress = null;
			retreiveCam = true;
		}
		void stop(){
			stop = true;
			retreiveCam = false;
			retreiveComm = false;
		}
		
		@Override
		public void run() {
			while(!stop){
				if(!retreiveCam && !retreiveComm){
					if(waitSource == null)
						waitSource = ()->{return retreiveCam || retreiveComm || stop;};
					FlashUtil.delayUntil(waitSource, 10000, 500);
					
					if(stop)
						break;
				}
				
				String commhost = PropertyHandler.getStringValue(PROP_HOST_ROBOT);
				String camhost = PropertyHandler.getStringValue(PROP_HOST_CAM);
				
				if(retreiveComm && commhost != null && !commhost.isEmpty()){
					try {
						commAddress = InetAddress.getByName(commhost);
						
						logger.info("Found host: "+commhost);
						logger.info("Address: "+commAddress.getHostAddress());
						
						retreiveComm = false;
					} catch (UnknownHostException e) {
						logger.log(Level.SEVERE, "Exception while resolving stdandard comm address", e);
					}
				}
				
				if(retreiveCam && camhost != null && !camhost.isEmpty()){
					try {
						if(camhost.equals(commhost) && commAddress != null)
							camAddress = commAddress;
						else
							camAddress = InetAddress.getByName(camhost);
						
						logger.info("Found host: "+camhost);
						logger.info("Address: "+camAddress.getHostAddress());
						
						retreiveCam = false;
					} catch (UnknownHostException e) {
						logger.log(Level.SEVERE, "Exception while resolving cam comm address", e);
					}
				}
				
				FlashUtil.delay(100);
			}
		}
	}
	private static class ConnectionTask implements Runnable{

		private static final int RECONNECTION_RATE = 5000;
		private static final int REACHABLE_WAIT = 500;
		
		private long lastConAttmp = -1;
		
		private boolean commInitialized = false;
		private boolean commSettingError = false;
		
		private boolean camInitialized = false;
		private boolean camSettingError = false;
		
		void resetComm() {
			commInitialized = false;
		}
		void resetCam() {
			camInitialized = false;
		}
		
		private void commUpdate(){
			if(!commInitialized){
				String host = PropertyHandler.getStringValue(PROP_HOST_ROBOT);
				String protocol = PropertyHandler.getStringValue(PROP_COMM_PROTOCOL);
				int localport = PropertyHandler.getIntegerValue(PROP_COMM_PORT_LOCAL);
				int remoteport = PropertyHandler.getIntegerValue(PROP_COMM_PORT_REMOTE);
				if(host == null || host.equals("") || protocol == null || protocol.equals("") || 
						(!protocol.equalsIgnoreCase("udp") && !protocol.equalsIgnoreCase("tcp"))) {
					if(!commSettingError){
						GUI.showMainErrorDialog("Failed to initialize communications: validate values of "+
								PROP_HOST_ROBOT + " and " + PROP_COMM_PROTOCOL);
						commSettingError = true;
						
						hostRetriever.resetComm();
					}
				}
				else if(localport < 100 || remoteport < 100){
					if(!commSettingError){
						GUI.showMainErrorDialog("Failed to initialize communications: validate values of "+
								PROP_COMM_PORT_LOCAL + ", " + PROP_COMM_PORT_REMOTE);
						commSettingError = true;
						
						hostRetriever.resetComm();
					}
				}else{
					InetAddress ad = hostRetriever.commAddress;
					
					if(ad != null){
						try {
							if(protocol.equals("udp"))
								commInterface = new UDPCommInterface(ad, localport, remoteport);
							else if(protocol.equals("tcp")){
								InetAddress local = FlashUtil.getLocalAddress(ad);
								commInterface = new TCPCommInterface(local, ad, localport, remoteport);
							}
							
							communications = new Communications("Robot-Communication", commInterface);
							communications.setSendableCreator(new FlashboardSendableCreator());
							communications.start();
							
							commInitialized = true;
							commSettingError = false;
						} catch (IOException e) {
							logger.log(Level.SEVERE, "Exception while initializing comm communications", e);
						}
					}
				}
			}else if(commInitialized && !commInterface.isConnected()){
				try {
					if(!commInterface.getRemoteAddress().isReachable(REACHABLE_WAIT)){
						communications.close();
						commInitialized = false;
						
						hostRetriever.resetComm();
						
						logger.info("Comm remote not reachable");
					}
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Exception while checking comm remote reachebility", e);
				}
			}
		}
		private void camUpdate(){
			if(!camInitialized){
				String host = PropertyHandler.getStringValue(PROP_HOST_CAM);
				int localcamport = PropertyHandler.getIntegerValue(PROP_CAM_PORT_LOCAL);
				int remotecamport = PropertyHandler.getIntegerValue(PROP_CAM_PORT_REMOTE);
				if(host == null || host.equals("")) {
					if(!camSettingError){
						GUI.showMainErrorDialog("Failed to initialize cam communications: validate values of "+
								PROP_HOST_CAM);
						camSettingError = true;
						
						hostRetriever.resetCam();
					}
				}
				else if(localcamport < 100 || remotecamport < 100){
					if(!camSettingError){
						GUI.showMainErrorDialog("Failed to initialize cam communications: validate values of "+
								PROP_CAM_PORT_LOCAL + ", " + PROP_CAM_PORT_REMOTE);
						camSettingError = true;
						
						hostRetriever.resetCam();
					}
				}else{
					
					InetAddress ad = hostRetriever.camAddress;
					
					if(ad != null){
						try {
							camClient = new CameraClient("Robot", localcamport, ad, remotecamport);
							camClient.addListener(camViewer);
							
							camInitialized = true;
							camSettingError = false;
						} catch (IOException e) {
							logger.log(Level.SEVERE, "Exception while initializing cam communication", e);
						}
					}
				}
			}
		}
		
		@Override
		public void run() {
			if(lastConAttmp < 0 || FlashUtil.millis() - lastConAttmp >= RECONNECTION_RATE){
				commUpdate();
				camUpdate();
				
				lastConAttmp = FlashUtil.millis();
			}
		}
	}
	private static class ConnectionTracker implements Runnable{
		
		private boolean commConnected = false;
		private boolean camConnected = false;
		
		@Override
		public void run() {
			if(commConnected != communicationsConnected()){
				commConnected = communicationsConnected();
				if(!commConnected){
					Dashboard.resetDisplaybles();
					GUI.resetWindows();
				}
				
				FlashFXUtils.onFXThread(()->{
					GUI.getMain().setCommConnected(commConnected);
				});
			}
			if(camConnected != camConnected()){
				camConnected = camConnected();
				
				FlashFXUtils.onFXThread(()->{
					GUI.getMain().setCamConnected(camConnected);
				});
			}
		}
	}
	
	//--------------------------------------------------------------------
	//-------------------------Props--------------------------------------
	//--------------------------------------------------------------------
	
	public static final String PROP_HOST_ROBOT = "host.robot";
	public static final String PROP_HOST_CAM = "host.cam";
	public static final String PROP_COMM_PROTOCOL = "comm.protocol";
	public static final String PROP_COMM_PORT_LOCAL = "comm.port.local";
	public static final String PROP_COMM_PORT_REMOTE = "comm.port.remote";
	public static final String PROP_CAM_PORT_LOCAL = "cam.port.local";
	public static final String PROP_CAM_PORT_REMOTE = "cam.port.remote";
	public static final String PROP_VISION_DEFAULT_PARAM = "vision.default";
	
	public static final String FOLDER_SAVES = "data/saves/";
	public static final String FOLDER_RESOURCE = "data/res/";
	public static final String FOLDER_DATA = "data/";
	public static final String FOLDER_LIBS_NATIVES = "libs/natives/";
	public static final String FOLDER_LIBS_NATIVES_CURRENT = FOLDER_LIBS_NATIVES + "current/";
	
	private static final String SETTINGS_FILE = FOLDER_DATA+"dash.xml";
	private static final String MODES_FILE = FOLDER_DATA+"modes.xml";
	
	
	private static boolean emptyProperty(String prop){
		String propv = PropertyHandler.getStringValue(prop);
		return propv == null || propv.isEmpty();
	}
	private static void validateBasicSettings() throws Exception{
		PropertyHandler.addString(PROP_VISION_DEFAULT_PARAM, "");
		PropertyHandler.addString(PROP_HOST_ROBOT, "");
		PropertyHandler.addString(PROP_HOST_CAM, "");
		if(emptyProperty(PROP_HOST_ROBOT))
			logger.severe("Missing Property: "+PROP_HOST_ROBOT);
		if(emptyProperty(PROP_HOST_CAM))
			logger.severe("Missing Property: "+PROP_HOST_ROBOT);
		
		PropertyHandler.addString(PROP_COMM_PROTOCOL, "tcp");
		String protocol = PropertyHandler.getStringValue(PROP_COMM_PROTOCOL);
		if(!protocol.equals("tcp") && 
				!protocol.equals("udp")){
			logger.severe("Invalid Property Value: "+PROP_COMM_PROTOCOL + "\nValues should be: tcp or udp");
		}
		
		PropertyHandler.addNumber(PROP_COMM_PORT_LOCAL, Flashboard.PORT_BOARD);
		PropertyHandler.addNumber(PROP_COMM_PORT_REMOTE, Flashboard.PORT_ROBOT);
		PropertyHandler.addNumber(PROP_CAM_PORT_LOCAL, Flashboard.CAMERA_PORT_BOARD);
		PropertyHandler.addNumber(PROP_CAM_PORT_REMOTE, Flashboard.CAMERA_PORT_ROBOT);
	}
	private static void loadSettings(){
		try {
			PropertyHandler.loadPropertyFromXml(SETTINGS_FILE);
		} catch (IllegalArgumentException e) { //TODO: minimize exception scope
			logger.log(Level.SEVERE, "Failed to load settings", e);
		}
	}
	private static void printSettings(){
		// TODO: print settings
	}
	private static void saveSettings(){
		PropertyHandler.savePropertiesToXml(SETTINGS_FILE);
	}

	//--------------------------------------------------------------------
	//-----------------------Display--------------------------------------
	//--------------------------------------------------------------------
	
	private static Vector<Displayable> displayables = new Vector<Displayable>();
	private static CameraViewer camViewer;
	
	public static void addDisplayable(Displayable displayable){
		displayables.addElement(displayable);
		updater.addTask(displayable);
	}
	public static void resetDisplaybles(){
		synchronized (displayables) {
			for (Displayable displayable : displayables) {
				updater.removeTask(displayable);
			}
			
			displayables.clear();
			addDisplayable(camViewer);
			addDisplayable(emergencyStop);
		}
		
		TesterControl.resetTesters();
		BarChartControl.resetControls();
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = GUI.initializeMainWindow(primaryStage);
		
		Scene scene = new Scene(root, 1300, 680);
		scene.addEventFilter(KeyEvent.KEY_PRESSED, (e)->{
			if(e.getCode() == KeyCode.SPACE){
				System.out.println("SPACE! THE FINAL FRONTIER!");
				
				if(emergencyStop != null){
					emergencyStop.change(true);
				}
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest((e)->{
			System.exit(0);
		});
		primaryStage.setTitle("FLASHboard");
		
		resetDisplaybles();
		startBaseTasks();
		
		primaryStage.show();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Updater--------------------------------------
	//--------------------------------------------------------------------
	
	private static Updater updater;
	
	public static Updater getUpdater(){
		return updater;
	}

	//--------------------------------------------------------------------
	//-----------------------Communications--------------------------------------
	//--------------------------------------------------------------------
	
	private static ConnectionTask connectionTask;
	
	private static HostRetrieverTask hostRetriever;
	
	private static IPCommInterface commInterface;
	private static Communications communications;
	private static CameraClient camClient;
	
	public static void restartCommunications(){
		if(communications != null){
			try {
				communications.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error while closing communications", e);
			}
			communications = null;
			connectionTask.resetComm();
			
			hostRetriever.resetComm();
			
			logger.info("Standard communication restart");
		}
		if(camClient != null){
			camClient.close();
			camClient = null;
			connectionTask.resetCam();
			
			hostRetriever.resetCam();
			
			logger.info("Camera communication restart");
		}
	}
	public static boolean communicationsConnected(){
		return communications != null && communications.isConnected();
	}
	public static boolean camConnected(){
		return camClient != null && camClient.isConnected();
	}
	public static CameraViewer getCamViewer(){
		return camViewer;
	}
	
	//--------------------------------------------------------------------
	//-----------------------Vision--------------------------------------
	//--------------------------------------------------------------------
	
	private static VisionRunner vision;
	
	public static Vision getVision(){
		return vision;
	}
	public static void closeVision(){
		if(visionInitialized()){
			vision.stop();
			if(vision instanceof ThreadedVisionRunner)
				((ThreadedVisionRunner)vision).close();
			vision = null;
		}
	}
	private static void loadVisionSaves(){
		logger.info("Loading vision files from saves folder...");
		File savesFolder = new File(FOLDER_SAVES);
		File[] files = savesFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		for (File file : files){
			
			FileInputStream inStream = null;
			ObjectInputStream objectInputStream = null;
			try{
				inStream = new FileInputStream(file);
				objectInputStream = new XMLObjectInputStream(inStream);
				
				Object deserializedObj = objectInputStream.readObject();
				
				if (deserializedObj instanceof VisionProcessing)
					vision.addProcessing((VisionProcessing) deserializedObj);
			}catch(IOException | ClassNotFoundException e){
				FlashUtil.getLogger().log(Level.SEVERE, "Failed to parse vision processing file", e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException e) {}
				}
				if (objectInputStream != null) {
					try {
						objectInputStream.close();
					} catch (IOException e) {}
				}
			}
		}
		
		String name = PropertyHandler.getStringValue(PROP_VISION_DEFAULT_PARAM);
		if(name != null){
			for (int i = 0; i < vision.getProcessingCount(); i++) {
				if(vision.getProcessing(i).getName().equals(name)){
					vision.selectProcessing(i);
				}
			}
		}
		logger.info("Done");
	}
	public static boolean visionInitialized(){
		return vision != null;
	}
	public static void setForVision(Object frame){
		vision.setFrame(frame);
	}
	
	//--------------------------------------------------------------------
	//--------------------------MAIN--------------------------------------
	//--------------------------------------------------------------------
	
	private static EmergencyStopControl emergencyStop;
	private static HIDControl hidcontrol;
	private static ModeSelectorControl modeSelectorControl;
	
	public static HIDControl getHIDControl(){
		return hidcontrol;
	}
	public static ModeSelectorControl getModeSelectorControl(){
		return modeSelectorControl;
	}
	public static EmergencyStopControl getEmergencyStopControl(){
		return emergencyStop;
	}
	
	private static void loadModes() {
	    File file = new File(MODES_FILE);
	    if(file.exists()){
	    	try {
				modeSelectorControl.loadModes(file);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to load states", e);
			}
	    }
	}
	
	//--------------------------------------------------------------------
	//-----------------------Init & Shut----------------------------------
	//--------------------------------------------------------------------
	
	private static final Logger logger = FlashUtil.getLogger();
	private static String currentNativesFolder = "";
	
	public static void main(String[] args) throws Exception{
		logger.info("Loading settings and properties...");
		validateBasicHierarcy();
		loadSettings();
		validateBasicSettings();
		printSettings();
		logger.info("Done");
		
		setupValuePath();
		logger.info("FlashLib version: "+FlashUtil.VERSION);
		logger.info("Loading opencv natives: "+Core.NATIVE_LIBRARY_NAME+" ...");
		loadValueLibrary(Core.NATIVE_LIBRARY_NAME);
		logger.info("opencv version: "+Core.VERSION);
		
		logger.info("Creating shutdown hook...");
		Runtime.getRuntime().addShutdownHook(new Thread(()->close()));
		logger.info("Done");
		
		initStart();
	    logger.info("Launching FX...");
	    launch();
	}
	private static void setupValuePath(){
		String path = FOLDER_LIBS_NATIVES;
		if(FlashUtil.isWindows()){
			if(FlashUtil.isArchitectureX64())
				path += "win64/";
			else 
				path += "win32/";
		}
		else if(FlashUtil.isUnix())
			path += "linux/";
		else
			throw new RuntimeException("Incompatible Operating System");
		
		path = new File(path).getAbsolutePath();
		logger.info("java.library.path="+path);
		currentNativesFolder = path;
	}
	private static void loadValueLibrary(String libname){
		String path = currentNativesFolder + File.separator;
		if(FlashUtil.isWindows())
			path += libname + ".dll";
		else if(FlashUtil.isUnix())
			path += "lib" + libname + ".so";
		System.load(path);
	}
	private static void initStart(){
		hostRetriever = new HostRetrieverTask();
		hostRetriever.resetCam();
		hostRetriever.resetComm();
		
		updater = new Updater();
	    camViewer = new CameraViewer("Robot-CamViewer");
	    hidcontrol = new HIDControl();
	    
	    modeSelectorControl = new ModeSelectorControl();
	    
	    connectionTask = new ConnectionTask();
	    emergencyStop = new EmergencyStopControl();
	    
	    vision = new ThreadedVisionRunner("flashboard-vision");
	    vision.setVisionSource(new CvSource());
		vision.getVisionSource().setImagePipeline(camViewer);
	}
	private static void startBaseTasks() {
		updater.addTask(hostRetriever);
		updater.addTask(new ConnectionTracker());
		updater.addTask(connectionTask);
		updater.addTask(hidcontrol);
		updater.execute(()->{
			loadVisionSaves();
			loadModes();
		});
	}
	private static void validateBasicHierarcy(){
		File file = new File(FOLDER_DATA);
		if(!file.exists()){
			if(!file.mkdir()){
				logger.severe("Unable to create DATA folder!!");
				return;
			}
		}
		file = new File(FOLDER_SAVES);
		if(!file.exists())
			file.mkdir();
		file = new File(FOLDER_RESOURCE);
		if(!file.exists())
			file.mkdir();
		file = new File(FOLDER_LIBS_NATIVES_CURRENT);
		if(!file.exists())
			file.mkdir();
	}
	public static void close(){
		logger.info("Shutting down");
		
		updater.shutdown();
		hostRetriever.stop();
		
		if(visionInitialized()){
			for (int i = 0; i < vision.getProcessingCount(); i++) {
				VisionProcessing proc = vision.getProcessing(i);
				
				String fileName = FOLDER_SAVES + proc.getName() + ".xml";
				
				FileOutputStream fileOutputStream = null;
				ObjectOutputStream objectOutputStream = null;
				try {
					fileOutputStream = new FileOutputStream(fileName);
					objectOutputStream = new XMLObjectOutputStream(fileOutputStream);
					
					objectOutputStream.writeObject(proc);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Failed to save processing object", e);
				} finally {
					if (fileOutputStream != null) {
						try {
							fileOutputStream.close();
						} catch (IOException e) {}
					}
					if (objectOutputStream != null) {
						try {
							objectOutputStream.close();
						} catch (IOException e) {}
					}
				}
			}
			
			logger.info("Stopping image processing...");
			closeVision();
		}
		if(camClient != null){
			logger.info("Stopping camera client...");
			camClient.close();
		}
		if(communications != null){
			logger.info("Stopping communications...");
			try {
				communications.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error while closing communications", e);
			}
		}
		if(!updater.isTerminated()){
			logger.info("Stopping update thread...");
			try {
				updater.forceShutdown();
			} catch (InterruptedException e) {
				logger.warning("Thread interrupted while stopping updater");
			}
		}
		
		if(modeSelectorControl != null){
			 File file = new File(MODES_FILE);
			 try {
				modeSelectorControl.saveModes(file);
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Failed to save modes", e);
			}
		}
		
		saveSettings();
		logger.info("Settings saved");
		
		for (Handler handler : logger.getHandlers()) {
			handler.flush();
			handler.close();
		}
	}
}
