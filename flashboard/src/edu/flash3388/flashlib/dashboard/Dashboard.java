package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.Vector;

import org.opencv.core.Core;

import edu.flash3388.flashlib.dashboard.controls.CameraViewer;
import edu.flash3388.flashlib.dashboard.controls.EmergencyStopControl;
import edu.flash3388.flashlib.dashboard.controls.HIDControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.communications.CameraClient;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.IPCommInterface;
import edu.flash3388.flashlib.communications.TCPCommInterface;
import edu.flash3388.flashlib.communications.UDPCommInterface;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.ThreadedVisionRunner;
import edu.flash3388.flashlib.vision.Vision;
import edu.flash3388.flashlib.vision.VisionFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;
import edu.flash3388.flashlib.vision.VisionRunner;
import edu.flash3388.flashlib.vision.cv.CvSource;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Dashboard extends Application {

	//--------------------------------------------------------------------
	//-----------------------Classes--------------------------------------
	//--------------------------------------------------------------------
	
	public static class Updater{
		private Scheduler scheduler = Scheduler.getInstance();
		private boolean stop = false;
		private Runnable task;
		
		public Updater() {
			task = ()->{
				while (!stop) {
					if(!fxInitialized()){
						FlashUtil.delay(100);
						continue;
					}
					scheduler.run();
					FlashUtil.delay(5);
				}
			};
		}
		
		public void addTask(Runnable runnable){
			scheduler.addTask(runnable);
		}
		public void execute(Runnable runnable){
			scheduler.execute(runnable);
		}
		public boolean removeTask(Runnable runnable){
			return scheduler.remove(runnable);
		}
		
		public void stop(){
			stop = true;
		}
		public Runnable getThreadTask(){
			return task;
		}
	}
	
	private static class ConnectionTask implements Runnable{

		private static final int RECONNECTION_RATE = 5000;
		private long lastConAttmp = -1;
		
		private boolean commInitialized = false;
		private boolean commSettingError = false;
		private boolean camInitialized = false;
		private boolean camSettingError = false;
		
		@Override
		public void run() {
			if(lastConAttmp < 0 || FlashUtil.millis() - lastConAttmp >= RECONNECTION_RATE){
				lastConAttmp = FlashUtil.millis();
				if(!commInitialized){
						String host = ConstantsHandler.getStringValue(PROP_HOST_ROBOT);
						String protocol = ConstantsHandler.getStringValue(PROP_COMM_PROTOCOL);
						int localport = ConstantsHandler.getIntegerValue(PROP_COMM_PORT_LOCAL);
						int remoteport = ConstantsHandler.getIntegerValue(PROP_COMM_PORT_REMOTE);
						if(host == null || host.equals("") || protocol == null || protocol.equals("") || 
								(!protocol.equalsIgnoreCase("udp") && !protocol.equalsIgnoreCase("tcp"))) {
							if(!commSettingError){
								userError("Failed to initialize communications: validate values of "+
										PROP_HOST_ROBOT + " and " + PROP_COMM_PROTOCOL);
								commSettingError = true;
							}
						}
						else if(localport < 100 || remoteport < 100){
							if(!commSettingError){
								userError("Failed to initialize communications: validate values of "+
										PROP_COMM_PORT_LOCAL + ", " + PROP_COMM_PORT_REMOTE);
								commSettingError = true;
							}
						}else{
							try {
								InetAddress ad = InetAddress.getByName(host);
								log.log("Found host: "+host, "Dashboard");
								if(protocol.equals("udp"))
									commInterface = new UDPCommInterface(ad, localport, remoteport);
								else if(protocol.equals("tcp")){
									InetAddress local = FlashUtil.getLocalAddress(ad);
									commInterface = new TCPCommInterface(local, ad, localport, remoteport);
								}
								
								communications = new Communications("Robot", commInterface);
								communications.setSendableCreator(new FlashboardSendableCreator());
								communications.start();
								
								commInitialized = true;
								commSettingError = false;
							} catch (IOException e) {
							}
						}
				}else if(commInitialized && !commInterface.isConnected()){
					try {
						if(!commInterface.getRemoteAddress().isReachable(RECONNECTION_RATE)){
							communications.close();
							commInitialized = false;
						}
					} catch (IOException e) {
					}
				}
				
				if(!camInitialized){
					String host = ConstantsHandler.getStringValue(PROP_HOST_CAM);
					int localcamport = ConstantsHandler.getIntegerValue(PROP_CAM_PORT_LOCAL);
					int remotecamport = ConstantsHandler.getIntegerValue(PROP_CAM_PORT_REMOTE);
					if(host == null || host.equals("")) {
						if(!camSettingError){
							userError("Failed to initialize cam communications: validate values of "+
									PROP_HOST_CAM);
							camSettingError = true;
						}
					}
					else if(localcamport < 100 || remotecamport < 100){
						if(!camSettingError){
							userError("Failed to initialize cam communications: validate values of "+
									PROP_CAM_PORT_LOCAL + ", " + PROP_CAM_PORT_REMOTE);
							camSettingError = true;
						}
					}else{
						try {
							InetAddress ad = InetAddress.getByName(host);
							
							camClient = new CameraClient("Robot", localcamport, ad, remotecamport);
							camClient.addListener(camViewer);
							
							camInitialized = true;
							camSettingError = false;
						} catch (IOException e) {
						}
					}
				}
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
	
	
	private static boolean emptyProperty(String prop){
		String propv = ConstantsHandler.getStringValue(prop);
		return propv == null || propv.equals("");
	}
	private static void validateBasicSettings() throws Exception{
		ConstantsHandler.addString(PROP_VISION_DEFAULT_PARAM, "");
		ConstantsHandler.addString(PROP_HOST_ROBOT, "");
		ConstantsHandler.addString(PROP_HOST_CAM, "");
		if(emptyProperty(PROP_HOST_ROBOT))
			log.reportError("Missing Property: "+PROP_HOST_ROBOT);
		if(emptyProperty(PROP_HOST_CAM))
			log.reportError("Missing Property: "+PROP_HOST_ROBOT);
		
		ConstantsHandler.addString(PROP_COMM_PROTOCOL, "tcp");
		if(!ConstantsHandler.getStringValue(PROP_COMM_PROTOCOL, "").equals("tcp") && 
				!ConstantsHandler.getStringValue(PROP_COMM_PROTOCOL, "").equals("udp"))
			log.reportError("Invalid Property Value: "+PROP_COMM_PROTOCOL + "\nValues are: tcp | udp");
		
		ConstantsHandler.addNumber(PROP_COMM_PORT_LOCAL, Flashboard.PORT_BOARD);
		ConstantsHandler.addNumber(PROP_COMM_PORT_REMOTE, Flashboard.PORT_ROBOT);
		ConstantsHandler.addNumber(PROP_CAM_PORT_LOCAL, Flashboard.CAMERA_PORT_BOARD);
		ConstantsHandler.addNumber(PROP_CAM_PORT_REMOTE, Flashboard.CAMERA_PORT_ROBOT);
	}
	private static void loadSettings(){
		try {
			ConstantsHandler.loadConstantsFromXml(SETTINGS_FILE);
		} catch (Exception e) {
			log.reportError(e.getMessage());
		}
	}
	private static void printSettings(){
		ConstantsHandler.printAll(log);
	}
	private static void saveSettings(){
		ConstantsHandler.saveConstantsToXml(SETTINGS_FILE);
	}

	//--------------------------------------------------------------------
	//-----------------------Display--------------------------------------
	//--------------------------------------------------------------------
	
	private static Vector<Displayble> displayables = new Vector<Displayble>();
	private static Stage primaryStage;
	private static CameraViewer camViewer;
	private static EmergencyStopControl emergencyStop;
	private static HIDControl hidcontrol;
	private static boolean fxready = false;
	
	private MainController controller;
	
	public static void updateParamDisplay(){
		Platform.runLater(()->{
			instance.controller.updateParam();
		});
	}
	public static Stage getPrimary(){
		return primaryStage;
	}
	
	public static Enumeration<Displayble> getDisplaybles(){
		return displayables.elements();
	}
	public static void addDisplayable(Displayble d){
		displayables.addElement(d);
	}
	public static void resetDisplaybles(){
		displayables.clear();
		closeVision();
		addDisplayable(camViewer);
		addDisplayable(hidcontrol);
	}
	
	public static HIDControl getHIDControl(){
		return hidcontrol;
	}
	
	private static void fxReady(){
		fxready = true;
	}
	private static boolean fxInitialized(){
		return fxready;
	}
	
	public static void userError(String error){
		log.reportError(error);
		FlashFxUtils.onFxThread(()->{
			FlashFxUtils.showErrorDialog(primaryStage, "Error", error);
		});
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Dashboard.primaryStage = primaryStage;
		Dashboard.instance = this;
		
		BorderPane root = new BorderPane();
		
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Dashboard.class.getResource("WorldWindow.fxml"));
			loader.setRoot(root);
			root = loader.load();
			controller = loader.getController(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Scene scene = new Scene(root, 1300, 680);
		scene.setOnKeyPressed((e)->{
			if(e.getCode() == KeyCode.SPACE && Dashboard.emergencyStop != null){
				System.out.println("SPACE! THE FINAL FRONTIER!");
				Dashboard.emergencyStop.change();
			}
		});
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(true);
		primaryStage.setOnCloseRequest((e)->{
			controller.stop();
			System.exit(0);
		});
		primaryStage.setTitle("FLASHboard");
		
		fxReady();
		primaryStage.show();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Updater--------------------------------------
	//--------------------------------------------------------------------
	
	private static Updater updater;
	private static Thread updateThread;
	
	public static Updater getUpdater(){
		return updater;
	}

	//--------------------------------------------------------------------
	//-----------------------Communications--------------------------------------
	//--------------------------------------------------------------------
	
	private static IPCommInterface commInterface;
	private static Communications communications;
	private static CameraClient camClient;
	
	public static boolean communicationsConnected(){
		return communications != null && communications.isConnected();
	}
	public static boolean camConnected(){
		return camClient != null && camClient.isConnected();
	}
	public static CameraViewer getCamViewer(){
		return camViewer;
	}
	
	protected static void setEmergencyStopControl(EmergencyStopControl estop){
		Dashboard.emergencyStop = estop;
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
		log.log("Loading vision files from saves folder...");
		File savesFolder = new File(FOLDER_SAVES);
		File[] files = savesFolder.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		
		for (File file : files) 
			instance.controller.loadParam(file.getAbsolutePath(), false);
		
		String name = ConstantsHandler.getStringValue(PROP_VISION_DEFAULT_PARAM);
		if(name != null){
			for (int i = 0; i < vision.getProcessingCount(); i++) {
				if(vision.getProcessing(i).getName().equals(name)){
					vision.selectProcessing(i);
					instance.controller.updateParamBoxSelection();
				}
			}
		}
		log.log("done");
	}
	public static boolean visionInitialized(){
		return vision != null;
	}
	public static void setForVision(Object frame){
		vision.setFrame(frame);
	}
	protected static void setVision(VisionRunner vision){
		Dashboard.vision = vision;
		vision.setVisionSource(new CvSource());
		vision.getVisionSource().setImagePipeline(camViewer);
		updater.execute(()->{
			loadVisionSaves();
		});
	}
	
	//--------------------------------------------------------------------
	//-----------------------Init & Shut----------------------------------
	//--------------------------------------------------------------------
	
	private static Dashboard instance;
	private static Log log;
	private static String currentNativesFolder = "";
	
	public static void main(String[] args) throws Exception{
		log = FlashUtil.getLog();
		
		log.log("Loading settings and properties...", "Dashboard");
		validateBasicHierarcy();
		loadSettings();
		validateBasicSettings();
		printSettings();
		log.log("Done", "Dashboard");
		
		setupValuePath();
		log.log("FlashLib version: "+FlashUtil.VERSION);
		log.log("Loading opencv natives: "+Core.NATIVE_LIBRARY_NAME+" ...", "Dashboard");
		loadValueLibrary(Core.NATIVE_LIBRARY_NAME);
		log.log("opencv version: "+Core.VERSION, "Dashboard");
		
		log.log("Loading jinput natives...", "Dashboard");
		loadJinputNatives();
		
		log.log("Creating shutdown hook...", "Dashboard");
		Runtime.getRuntime().addShutdownHook(new Thread(()->close()));
		log.log("Done", "Dashboard");
		
		log.save();
		initStart();
	    log.log("Launching FX...", "Dashboard");
	    launch();
	}
	private static void loadJinputNatives(){
		String nativeName = "";
		String folder = FOLDER_LIBS_NATIVES;
		if(FlashUtil.isUnix()){
			folder += "linux/";
			if(FlashUtil.isArchitectureX64())
				nativeName = "libjinput-linux64.so";
			else
				nativeName = "libjinput-linux.so";
		}else{
			if(FlashUtil.isArchitectureX64()){
				folder += "win64/";
				nativeName += "jinput-raw_64.dll";
			}
			else{
				folder += "win32/";
				nativeName += "jinput-raw.dll";
			}
		}
		
		
		try {
			Files.copy(Paths.get(folder + nativeName), Paths.get(FOLDER_LIBS_NATIVES_CURRENT + nativeName), 
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
		}
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
		log.log("java.library.path="+path, "Dashboard");
		currentNativesFolder = path;
	}
	private static void loadValueLibrary(String libname){
		String path = currentNativesFolder+"/";
		if(FlashUtil.isWindows())
			path += libname+".dll";
		else if(FlashUtil.isUnix())
			path += "lib"+libname+".so";
		System.load(path);
	}
	private static void initStart(){
		VisionFilter.setFilterCreator(new DefaultFilterCreator());
		updater = new Updater();
		updateThread = new Thread(updater.getThreadTask());
	    updateThread.start();
	    camViewer = new CameraViewer("Robot-CamViewer");
	    addDisplayable(camViewer);
	    hidcontrol = new HIDControl();
	    addDisplayable(hidcontrol);
	    updater.addTask(new ConnectionTask());
	    //updater.addTask(new VisionRunnerDataTask());
	}
	private static void validateBasicHierarcy(){
		File file = new File(FOLDER_DATA);
		if(!file.exists()){
			if(!file.mkdir()){
				log.reportError("Unable to create DATA folder!!");
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
		log.log("Shutting down");
		updater.stop();
		if(visionInitialized()){
			for (int i = 0; i < vision.getProcessingCount(); i++) {
				VisionProcessing proc = vision.getProcessing(i);
				proc.saveXml(FOLDER_SAVES+proc.getName()+".xml");
			}
			
			log.log("Stopping image processing...");
			closeVision();
		}
		if(camClient != null){
			log.log("Stopping camera client...");
			camClient.stop();
		}
		if(communications != null){
			log.log("Stopping communications...");
			communications.close();
		}
		if(updateThread.isAlive()){
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		
		File natives = new File(FOLDER_LIBS_NATIVES_CURRENT);
		File[] files = natives.listFiles();
		for (File file : files) {
			file.delete();
		}
		
		saveSettings();
		log.log("Settings saved");
		log.close();
		Platform.exit();
	}
}
