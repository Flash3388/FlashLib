package edu.flash3388.flashlib.dashboard;

import java.io.File;

import edu.flash3388.flashlib.gui.FlashFXUtils;
import edu.flash3388.flashlib.gui.PropertyViewer;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class GUI {
	private GUI(){}
	
	private static Stage primary;
	private static MainWindow mainWindow;
	
	public static Stage getPrimary(){
		return primary;
	}
	public static MainWindow getMain(){
		return mainWindow;
	}
	
	static Parent initializeMainWindow(Stage primary){
		GUI.primary = primary;
		
		mainWindow = new MainWindow();
		return mainWindow.initializeMainScene();
	}
	
	public static void resetWindows(){
		Platform.runLater(()->{
			mainWindow.resetWindow();
			PDPWindow.reset();
			TesterWindow.resetTester();
			PIDTunerWindow.reset();
			LogWindow.resetLogs();
		});
	}
	
	public static void showMainErrorDialog(String error){
		FlashUtil.getLog().reportError(error);
		FlashFXUtils.onFXThread(()->{
			FlashFXUtils.showErrorDialog(primary, "Error", error);
		});
	}
	
	public static void showLogDialog(){
		LogWindow.showLog();
	}
	public static void showPDPWindow(){
		PDPWindow.showPDP();
	}
	public static void showMotorTester(){
		TesterWindow.showTester();
	}
	public static void showPIDTuner(){
		PIDTunerWindow.showTuner();
	}
	public static void showHIDControl(){
		HIDWindow.showHIDWindow();
	}
	public static void showModeSelector(){
		ModeSelectorWindow.showModeSelector();
	}
	public static void showPropertiesViewer(){
		PropertyViewer.showPropertyViewer(primary);
	}
	public static void showVisionEditor(){
		VisionEditorWindow.showEditor(Dashboard.getVision());
	}
	public static File showVisionLoadDialog(){
		FileChooser chooser = new FileChooser();
		ExtensionFilter extFilter = new ExtensionFilter("Vision File", "*.xml");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setSelectedExtensionFilter(extFilter);
		chooser.setInitialDirectory(new File(Dashboard.FOLDER_SAVES));
		
		return chooser.showOpenDialog(primary);
	}
	public static File showVisionSaveDialog(){
		FileChooser chooser = new FileChooser();
		ExtensionFilter extFilter = new ExtensionFilter("Vision File", "*.xml");
		chooser.getExtensionFilters().add(extFilter);
		chooser.setSelectedExtensionFilter(extFilter);
		chooser.setInitialDirectory(new File(Dashboard.FOLDER_SAVES));
		
		return chooser.showSaveDialog(primary);
	}
}
