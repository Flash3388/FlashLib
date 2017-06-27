package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import edu.flash3388.flashlib.dashboard.controls.CameraViewer;
import edu.flash3388.flashlib.dashboard.controls.EmergencyStopControl;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.gui.Dialog;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.communications.CameraClient;
import edu.flash3388.flashlib.communications.CommInterface;
import edu.flash3388.flashlib.communications.Communications;
import edu.flash3388.flashlib.communications.TcpCommInterface;
import edu.flash3388.flashlib.communications.UdpCommInterface;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.CvProcessing;
import edu.flash3388.flashlib.vision.CvRunner;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.ProcessingFilter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Dashboard extends Application {

	private static class UpdateTask implements Runnable{
		Vector<UpdateWrapper> updatables = new Vector<UpdateWrapper>();
		boolean stop = false;
		boolean fxReady = false;
		@Override
		public void run() {
			while(!fxReady) FlashUtil.delay(50);
			while (!stop) {
				for(Enumeration<UpdateWrapper> upenum = updatables.elements(); upenum.hasMoreElements();){
					UpdateWrapper up = upenum.nextElement();
					up.runnable.run();
					if(up.removeWhenFinished)
						updatables.remove(up);
				}
				FlashUtil.delay(3);
			}
		}
	}
	private static class UpdateWrapper{
		Runnable runnable;
		boolean removeWhenFinished;
	}
	
	public static final String PROP_HOST_ROBOT = "host.robot";
	public static final String PROP_COMM_PROTOCOL = "comm.protocol";
	public static final String PROP_COMM_PORT_LOCAL = "comm.port.local";
	public static final String PROP_COMM_PORT_REMOTE = "comm.port.remote";
	public static final String PROP_CAM_PORT_LOCAL = "cam.port.local";
	public static final String PROP_CAM_PORT_REMOTE = "cam.port.remote";
	
	public static final String PROP_VISION_DEFAULT_PARAM = "vision.default";
	
	public static final String FOLDER_SAVES = "data/saves/";
	public static final String FOLDER_RESOURCE = "data/res/";
	public static final String FOLDER_DATA = "data/";
	
	private static final String SETTINGS_FILE = FOLDER_DATA+"dash.xml";
	private static final String REMOTE_HOSTS_FILE = FOLDER_DATA+"hosts.ini";
	
	private static Vector<Displayble> displayables = new Vector<Displayble>();
	private static Stage primaryStage;
	private static Dashboard instance;
	private static UpdateTask updateTask;
	private static Thread updateThread;
	private static CommInterface commInterface;
	private static Communications communications;
	private static CameraClient camClient;
	private static CameraViewer camViewer;
	private static CvRunner vision;
	private static EmergencyStopControl emergencyStop;
	private static Log log;
	
	private static byte[][] visionImageNext = new byte[2][2];
	private static int visionImageIndex = 0;
	private static boolean visionParamLoadFailed = false;
	
	private static boolean commSettingError = false;
	
	public static void main(String[] args) throws Exception{
		FlashUtil.setStart();
		log = FlashUtil.getLog();
		log.log("FlashLib version: "+FlashUtil.VERSION);
		Remote.initializeJSCH();
		log.log("Loading opencv natives: "+Core.NATIVE_LIBRARY_NAME+" ...", "Dashboard");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		log.log("opencv version: "+Core.VERSION, "Dashboard");
		log.log("Loading settings and properties...", "Dashboard");
		validateBasicHierarcy();
		loadSettings();
		validateBasicSettings();
		printSettings();
		log.log("Done", "Dashboard");
		
		log.log("Creating shutdown hook...", "Dashboard");
		Runtime.getRuntime().addShutdownHook(new Thread(()->close()));
		log.log("Done", "Dashboard");
		
		log.save();
		initStart();
	    log.log("Launching FX...", "Dashboard");
	    launch();
	}
	private static void initStart(){
		ProcessingFilter.setFilterCreator(new DefaultFilterCreator());
		updateTask = new UpdateTask();
		updateThread = new Thread(updateTask);
	    updateThread.start();
	    camViewer = new CameraViewer("Robot-CamViewer", -1);
	    addDisplayable(camViewer);
		addRunnableForUpdate(()->{
			   if(commInterface == null){
				   try {
					String host = ConstantsHandler.getStringNative(PROP_HOST_ROBOT);
					String protocol = ConstantsHandler.getStringNative(PROP_COMM_PROTOCOL);
					int localport = ConstantsHandler.getIntegerNative(PROP_COMM_PORT_LOCAL);
					int remoteport = ConstantsHandler.getIntegerNative(PROP_COMM_PORT_REMOTE);
					int localcamport = ConstantsHandler.getIntegerNative(PROP_CAM_PORT_LOCAL);
					int remotecamport = ConstantsHandler.getIntegerNative(PROP_CAM_PORT_REMOTE);
					if(host == null || host.equals("") || protocol == null || protocol.equals("") || 
							(!protocol.equalsIgnoreCase("udp") && !protocol.equalsIgnoreCase("tcp"))) {
						if(!commSettingError){
							userError("Failed to initialize communications: validate values of "+
									PROP_HOST_ROBOT + " and " + PROP_COMM_PROTOCOL);
							commSettingError = true;
						}
						
						return;
					}
					if(localport < 100 || remoteport < 100 || localcamport < 100 || remotecamport < 100){
						if(!commSettingError){
							userError("Failed to initialize communications: validate values of "+
									PROP_COMM_PORT_LOCAL + ", " + PROP_COMM_PORT_REMOTE + ", " + PROP_CAM_PORT_LOCAL +
									" and " + PROP_CAM_PORT_REMOTE);
							commSettingError = true;
						}
						
						return;
					}
					
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
					camClient = new CameraClient("Robot", localcamport, ad, remotecamport);
					camClient.addListener(camViewer);
				   }catch(java.net.UnknownHostException eh){
					   
				   }catch (IOException e) {
					   log.reportError(e.getMessage());
				   }
			   }
		 });
		addRunnableForUpdate(()->{
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
	public static void addRunnableForUpdate(Runnable run){
		UpdateWrapper up = new UpdateWrapper();
		up.runnable = run;
		up.removeWhenFinished = false;
		updateTask.updatables.addElement(up);
	}
	public static void runLater(Runnable run){
		UpdateWrapper up = new UpdateWrapper();
		up.runnable = run;
		up.removeWhenFinished = true;
		updateTask.updatables.addElement(up);
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
	public static void loadDefaultParameters(){
		if(vision == null || visionParamLoadFailed) return;
		String filename = ConstantsHandler.getStringNative(PROP_VISION_DEFAULT_PARAM);
		if(filename != null){
			log.log("Loading default parameters: "+filename, "Dashboard");
			visionParamLoadFailed = !instance.controller.loadParam(FOLDER_SAVES+filename);
		}
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
	}
	protected static void setEmergencyStopControl(EmergencyStopControl estop){
		Dashboard.emergencyStop = estop;
	}
	private static boolean emptyProperty(String prop){
		String propv = ConstantsHandler.getStringNative(prop);
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
		if(!ConstantsHandler.hasString(PROP_VISION_DEFAULT_PARAM))
			ConstantsHandler.putString(PROP_VISION_DEFAULT_PARAM, "");
		if(!ConstantsHandler.hasString(PROP_HOST_ROBOT)){
			ConstantsHandler.putString(PROP_HOST_ROBOT, "");
			log.reportError("Missing Property: "+PROP_HOST_ROBOT);
		}
		
		if(!ConstantsHandler.hasString(PROP_COMM_PROTOCOL))
			ConstantsHandler.putString(PROP_COMM_PROTOCOL, "tcp");
		else if(!ConstantsHandler.getStringNative(PROP_COMM_PROTOCOL).equals("tcp") && 
				!ConstantsHandler.getStringNative(PROP_COMM_PROTOCOL).equals("udp"))
			log.reportError("Invalid Property Value: "+PROP_COMM_PROTOCOL + "\nValues are: tcp | udp");
		
		if(emptyProperty(PROP_COMM_PORT_LOCAL))
			ConstantsHandler.putNumber(PROP_COMM_PORT_LOCAL, Flashboard.PORT_BOARD);
		if(emptyProperty(PROP_COMM_PORT_REMOTE))
			ConstantsHandler.putNumber(PROP_COMM_PORT_REMOTE, Flashboard.PORT_ROBOT);
		if(emptyProperty(PROP_CAM_PORT_LOCAL))
			ConstantsHandler.putNumber(PROP_CAM_PORT_LOCAL, Flashboard.CAMERA_PORT_BOARD);
		if(emptyProperty(PROP_CAM_PORT_REMOTE))
			ConstantsHandler.putNumber(PROP_CAM_PORT_REMOTE, Flashboard.CAMERA_PORT_ROBOT);
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
		updateTask.fxReady = true;
	}
	
	public static void userError(String error){
		log.reportError(error);
		FlashFxUtils.onFxThread(()->{
			Dialog.show(primaryStage, "Error", error);
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
		updateTask.stop = true;
		if(visionInitialized()){
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
		saveSettings();
		log.log("Settings saved");
		log.close();
		Platform.exit();
	}
}
