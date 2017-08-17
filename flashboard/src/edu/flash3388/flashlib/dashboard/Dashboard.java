package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.flash3388.flashlib.dashboard.controls.CameraViewer;
import edu.flash3388.flashlib.dashboard.controls.EmergencyStopControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.communications.CameraClient;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.IpCommInterface;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.cv.CvProcessing;
import edu.flash3388.flashlib.vision.cv.CvRunner;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.VisionFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Dashboard extends Application {

	public static class Updater{
		private Scheduler scheduler = new Scheduler();
		private boolean stop = false;
		private Runnable task;
		
		public Updater() {
			task = ()->{
				while(!fxInitialized() && !stop) 
					FlashUtil.delay(100);
				while (!stop) {
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
		
		public void addAction(Action action){
			scheduler.add(action);
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
									commInterface = new UdpCommInterface(ad, localport, remoteport);
								else if(protocol.equals("tcp")){
									InetAddress local = FlashUtil.getLocalAddress(ad);
									commInterface = new TcpCommInterface(local, ad, localport, remoteport);
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
	
	private static final String SETTINGS_FILE = FOLDER_DATA+"dash.xml";
	private static final String REMOTE_HOSTS_FILE = FOLDER_DATA+"hosts.ini";
	
	private static Vector<Displayble> displayables = new Vector<Displayble>();
	private static Stage primaryStage;
	private static Dashboard instance;
	private static Updater updater;
	private static Thread updateThread;
	private static IpCommInterface commInterface;
	private static Communications communications;
	private static CameraClient camClient;
	private static CameraViewer camViewer;
	private static CvRunner vision;
	private static EmergencyStopControl emergencyStop;
	private static Log log;
	
	private static boolean fxready = false;
	
	private static byte[][] visionImageNext = new byte[2][2];
	private static int visionImageIndex = 0;
	
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
		Remote.initializeJSCH();
		log.log("Loading opencv natives: "+Core.NATIVE_LIBRARY_NAME+" ...", "Dashboard");
		loadValueLibrary(Core.NATIVE_LIBRARY_NAME);
		log.log("opencv version: "+Core.VERSION, "Dashboard");
		
		log.log("Creating shutdown hook...", "Dashboard");
		Runtime.getRuntime().addShutdownHook(new Thread(()->close()));
		log.log("Done", "Dashboard");
		
		log.save();
		initStart();
	    log.log("Launching FX...", "Dashboard");
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
		log.log("java.library.path="+path, "Dashboard");
		System.setProperty("java.library.path", path);
	}
	private static void loadValueLibrary(String libname){
		String path = System.getProperty("java.library.path")+"/";
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
	    camViewer = new CameraViewer("Robot-CamViewer", -1);
	    addDisplayable(camViewer);
	    updater.addTask(new ConnectionTask());
	    updater.addTask(()->{
			byte[] img = visionImageNext[visionImageIndex];
			visionImageNext[visionImageIndex] = null;
			visionImageIndex ^= 1;
			if(vision != null && img != null){
				Mat m = CvProcessing.byteArray2Mat(img);
				vision.newImage(m, (byte)0);
			}
		});
	}

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
	public static void resetDisplaybles(){
		displayables.clear();
		if(visionInitialized()){
			vision.close();
			vision = null;
		}
		addDisplayable(camViewer);
	}
	public static Updater getUpdater(){
		return updater;
	}
	public static void addDisplayable(Displayble d){
		displayables.addElement(d);
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
	public static CvRunner getVision(){
		return vision;
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
	public static void setForVision(byte[] image){
		if(vision != null)
			visionImageNext[1-visionImageIndex] = image;
	}
	protected static void setVision(CvRunner vision){
		Dashboard.vision = vision;
		vision.setPipeline(camViewer);
		if(vision != null){
			updater.execute(()->{
				loadVisionSaves();
			});
		}
	}
	protected static void setEmergencyStopControl(EmergencyStopControl estop){
		Dashboard.emergencyStop = estop;
	}
	private static boolean emptyProperty(String prop){
		String propv = ConstantsHandler.getStringValue(prop);
		return propv == null || propv.equals("");
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
			Remote.loadHosts(REMOTE_HOSTS_FILE);
		} catch (Exception e) {
			log.reportError(e.getMessage());
		}
	}
	private static void printSettings(){
		ConstantsHandler.printAll(log);
	}
	private static void saveSettings(){
		ConstantsHandler.saveConstantsToXml(SETTINGS_FILE);
		Remote.saveHosts(REMOTE_HOSTS_FILE);
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
	
	private MainController controller;
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
	public static void close(){
		log.log("Shutting down");
		updater.stop();
		if(visionInitialized()){
			for (int i = 0; i < vision.getProcessingCount(); i++) {
				VisionProcessing proc = vision.getProcessing(i);
				proc.saveXml(FOLDER_SAVES+proc.getName()+".xml");
			}
			
			log.log("Stopping image processing...");
			vision.close();
		}
		if(camClient != null){
			log.log("Stopping camera client...");
			camClient.stop();
		}
		if(communications != null){
			log.log("Stopping communications...");
			communications.close();
		}
		Remote.closeSessions();
		if(updateThread.isAlive()){
			try {
				updateThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
		saveSettings();
		log.log("Settings saved");
		log.close();
		Platform.exit();
	}
}
