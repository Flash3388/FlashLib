package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import edu.flash3388.flashlib.dashboard.Displayble.DisplayType;
import edu.flash3388.flashlib.dashboard.controls.FlashboardTester;
import edu.flash3388.flashlib.dashboard.controls.PDP;
import edu.flash3388.flashlib.dashboard.controls.CameraViewer.DisplayMode;
import edu.flash3388.flashlib.dashboard.controls.DashboardPidTuner;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.gui.PropertyViewer;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.cv.CvRunner;
import edu.flash3388.flashlib.vision.VisionFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class MainController implements Initializable{

	private static class UpdateTask implements Runnable{
		MainController controller;
		boolean lastconnectionC = false, lastconnectionS = false;
		int lastProcCount = 0, lastProc = -1;
		Runnable dxUpdate = ()->{
			if(Dashboard.communicationsConnected())
				controller.update(); 
			CvRunner vision = Dashboard.getVision();
			if(vision != null){
				if(vision.getProcessingCount() != lastProcCount){
					for (int i = lastProcCount; i < vision.getProcessingCount(); i++)
						controller.addParamToBox(vision.getProcessing(i).getName());
					lastProcCount = vision.getProcessingCount();
				}
				if(vision.getProcessing() != null)
					controller.updateParam();
					
				if(lastProc != Dashboard.getVision().getSelectedProcessingIndex()){
					lastProc = Dashboard.getVision().getSelectedProcessingIndex();
					controller.updateParamBoxSelection();
				}
				controller.updateVisionRun();
			}
			if(Dashboard.communicationsConnected() != lastconnectionC){
				lastconnectionC = !lastconnectionC;
				controller.connectionRect.setFill(lastconnectionC ? Color.GREEN : Color.RED);
				
				if(!lastconnectionC){
					controller.resetDisplay();
					PDPWindow.reset();
					PDP.resetBoards();
					TesterWindow.closeTester();
					VisionEditorWindow.closeEditor();
					PidTunerWindow.reset();
					FlashboardTester.resetTesters();
					DashboardPidTuner.resetTuners();
				}
			}
			if(Dashboard.camConnected() != lastconnectionS){
				lastconnectionS = !lastconnectionS;
				controller.camserverRect.setFill(lastconnectionS ? Color.GREEN : Color.RED);
			}
			if(PDPWindow.onScreen())
				PDPWindow.getInstance().getController().update();
		};
		@Override
		public void run() {
			FlashFxUtils.onFxThread(dxUpdate);
		}
	}
	
	@FXML Slider h_min;
	@FXML Slider h_max;
	@FXML Slider s_min;
	@FXML Slider s_max;
	@FXML Slider v_min;
	@FXML Slider v_max;
	@FXML Label h_min_val, h_min_lbl;
	@FXML Label h_max_val, h_max_lbl;
	@FXML Label s_min_val, s_min_lbl;
	@FXML Label s_max_val, s_max_lbl;
	@FXML Label v_min_val, v_min_lbl;
	@FXML Label v_max_val, v_max_lbl;
	@FXML TextField h_min_text;
	@FXML TextField h_max_text;
	@FXML TextField s_min_text;
	@FXML TextField s_max_text;
	@FXML TextField v_min_text;
	@FXML TextField v_max_text;
	@FXML CheckBox hsv_check, vision_check;
	@FXML ComboBox<String> mode_box;
	@FXML Rectangle camserverRect, connectionRect;
	@FXML VBox controller_node, sensor_node, camera_node;
	@FXML HBox manual_controls;
	@FXML ToolBar camera_toolbar;
	@FXML ChoiceBox<String> displayBoxType;
	@FXML MenuItem motorTester, show_log, load_params, showpdp, save_params, prop_viewer, sbc_update, sbc_load,
				   sbc_ssh, sbc_sftp, sbc_controller, pidtuner, vision_editor;
	
	private UpdateTask threadTask;
	private boolean local = false;
	private boolean hsvSet = false;
	private ColorFilter colorFilter;
	
	public HBox getManualControlsNode(){
		return manual_controls;
	}
	public VBox getDataControlsNode(){
		return sensor_node;
	}
	public VBox getControllersNode(){
		return controller_node;
	}
	public VBox getCameraNode(){
		return camera_node;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		threadTask = new UpdateTask();
		threadTask.controller = this;
		Dashboard.addRunnableForUpdate(threadTask);
		
		sbc_ssh.setOnAction((e)->{
			//ShellWindow.showShellWindow(Dashboard.getPrimary(), ChannelType.SSH);
		});
		sbc_sftp.setOnAction((e)->{
			//ShellWindow.showShellWindow(Dashboard.getPrimary(), ChannelType.SFTP);
		});
		sbc_update.setOnAction((e)->{
			//TODO: IMPLEMENT
		});
		load_params.setOnAction((e)->{
			loadParams();
		});
		save_params.setOnAction((e)->{
			saveParams();
		});
		vision_editor.setOnAction(e ->{
			showVisionEditor();
		});
		motorTester.setOnAction((e)->showTester());
		pidtuner.setOnAction((e)->showPidTuner());
		show_log.setOnAction((e)->showLog());
		showpdp.setOnAction((e)->showPDP());
		prop_viewer.setOnAction((e)->showPropViewer());
		
		displayBoxType.getItems().addAll("Normal", "Post-Process", "Threshold");
		displayBoxType.getSelectionModel().select(0);
		displayBoxType.valueProperty().addListener((observable, oldValue, newValue)->{
			int index = displayBoxType.getSelectionModel().getSelectedIndex();
			Dashboard.getCamViewer().setDisplayMode(DisplayMode.values()[index]);
		});
		mode_box.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			if(local || oldValue == null || oldValue.intValue() == newValue.intValue())
				return;
			Dashboard.getVision().selectProcessing(newValue.intValue());
		});
		vision_check.setOnAction((e)->{
			if(!checkVision()) return;
			
			if(!Dashboard.getVision().isRunning() && vision_check.isSelected())
				Dashboard.getVision().start();
			else if(Dashboard.getVision().isRunning() && !vision_check.isSelected())
				Dashboard.getVision().stop();
		});
		hsv_check.setOnAction((e)->{
			if(!checkVision()) return;
			
			colorFilter.hsvProperty().set(hsv_check.isSelected());
			setForHSV(hsv_check.isSelected());
		});
		h_min.setMin(0);
		h_min.setMax(255);
		h_min.setValue(0);
		h_min_val.setText(String.valueOf(0));
		h_min.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val) {
				if(!checkVision()) return;
				
				colorFilter.min1Property().set(new_val.intValue());
				h_min_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		h_min_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = h_min_text.getText();
					h_min_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > (hsvSet? 180 : 255)){
						return;
					}
					h_min.setValue(ival);
				}
			}
		});
		
		h_max.setMin(0);
		h_max.setMax(255);
		h_max.setValue(255);
		h_max_val.setText(String.valueOf(255));
		h_max.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val){
				if(!checkVision()) return;
				
				colorFilter.max1Property().set(new_val.intValue());
				h_max_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		h_max_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = h_max_text.getText();
					h_max_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > (hsvSet? 180 : 255)){
						return;
					}
					h_max.setValue(ival);
				}
			}
		});
		
		s_min.setMin(0);
		s_min.setMax(255);
		s_min.setValue(0);
		s_min_val.setText(String.valueOf(0));
		s_min.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val){
				if(!checkVision()) return;
				
				colorFilter.min2Property().set(new_val.intValue());
				s_min_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		s_min_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = s_min_text.getText();
					s_min_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > 255){
						return;
					}
					s_min.setValue(ival);
				}
			}
		});
		
		s_max.setMin(0);
		s_max.setMax(255);
		s_max.setValue(255);
		s_max_val.setText(String.valueOf(255));
		s_max.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val){
				if(!checkVision()) return;
				
				colorFilter.max2Property().set(new_val.intValue());
				s_max_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		s_max_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = s_max_text.getText();
					s_max_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > 255){
						return;
					}
					s_max.setValue(ival);
				}
			}
		});
		
		v_min.setMin(0);
		v_min.setMax(255);
		v_min.setValue(0);
		v_min_val.setText(String.valueOf(0));
		v_min.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val){
				if(!checkVision()) return;
				
				colorFilter.min3Property().set(new_val.intValue());
				v_min_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		v_min_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = v_min_text.getText();
					v_min_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > 255){
						return;
					}
					v_min.setValue(ival);
				}
			}
		});
		
		v_max.setMin(0);
		v_max.setMax(255);
		v_max.setValue(255);
		v_max_val.setText(String.valueOf(255));
		v_max.valueProperty().addListener(new ChangeListener<Number>(){
			@Override
			public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val){
				if(!checkVision()) return;
				
				colorFilter.max3Property().set(new_val.intValue());
				v_max_val.setText(String.valueOf(new_val.intValue()));
			}
		});
		v_max_text.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode() == KeyCode.ENTER){
					String val = v_max_text.getText();
					v_max_text.setText("");
					if(!isANumber(val)){
						return;
					}
					int ival = Integer.parseInt(val);
					if(ival < 0 || ival > 255){
						return;
					}
					v_max.setValue(ival);
				}
			}
		});
	}
	
	private void saveParams(){
		if(Dashboard.getVision() == null){
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Vision was not set!\nCannot save parameters");
			return;
		}
		if(Dashboard.getVision().getProcessing() != null){
			FileChooser chooser = new FileChooser();
			chooser.setSelectedExtensionFilter(new ExtensionFilter("Filter File", ".xml"));
			chooser.setInitialDirectory(new File(Dashboard.FOLDER_SAVES));
			File file = chooser.showSaveDialog(Dashboard.getPrimary());
			if(file == null) return;
			String path = file.getAbsolutePath();
			FlashUtil.getLog().log("Saving parameters: "+path);
			Dashboard.getVision().getProcessing().saveXml(path);
		}
	}
	private void loadParams(){
		if(Dashboard.getVision() == null){
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Vision was not set!\nCannot load parameters");
			return;
		}
		FileChooser chooser = new FileChooser();
		chooser.setSelectedExtensionFilter(new ExtensionFilter("Filter File", ".xml"));
		chooser.setInitialDirectory(new File(Dashboard.FOLDER_SAVES));
		File file = chooser.showOpenDialog(Dashboard.getPrimary());
		if(file == null) return;
		String path = file.getAbsolutePath();
		loadParam(path, true);
	}
	public boolean loadParam(String path, boolean show){
		FlashUtil.getLog().log("Loading file: "+path);
		VisionProcessing proc = null;
		try {
			proc = VisionProcessing.createFromXml(path);
		} catch (Throwable e) {
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "XML Parsing Error", e.getMessage());
			
			FlashUtil.getLog().reportError(e.getMessage());
			FlashUtil.getLog().log("Loading failed");
			return false;
		}
		
		if(proc != null){
			FlashUtil.getLog().log("Parameters loaded");
			Dashboard.getVision().addProcessing(proc);
			Platform.runLater(()->updateParam());
			addParamToBox(proc.getName());
			return true;
		}else {
			FlashUtil.getLog().log("Loading failed");
			return false;
		}
	}
	private void addParamToBox(String name){
		for (String str : mode_box.getItems()) {
			if(name.equals(str)){
				return;
			}
		}
		Platform.runLater(()->{
			mode_box.getItems().add(name);
			mode_box.getSelectionModel().select(name);
		});
	}
	public void updateParamBoxSelection(){
		Platform.runLater(()->{
			local = true;
			mode_box.getSelectionModel().select(Dashboard.getVision().getSelectedProcessingIndex());
			local = false;
		});
	}
	private boolean checkVision(){
		if(local) 
			return false;
		CvRunner vision = Dashboard.getVision();
		if(vision == null) 
			return false;
		if(vision.getProcessing() == null)
			setParam();
		if(colorFilter == null)
			return false;
		return true;
	}
	private boolean isANumber(String s){
		for(char c : s.toCharArray()){
			if(c != '.' && !Character.isDigit(c))
				return false;
		}
		return true;
	}
	private void showPropViewer(){
		PropertyViewer.showPropertyViewer(Dashboard.getPrimary());
	}
	private void showPDP(){
		PDPWindow.showPDP();
	}
	private void showLog(){
		LogWindow.showLog();
	}
	private void showTester(){
		TesterWindow.showTester();
	}
	private void showPidTuner(){
		PidTunerWindow.showTuner();
	}
	private void showVisionEditor(){
		if(Dashboard.visionInitialized())
			VisionEditorWindow.showEditor(Dashboard.getVision());
		else FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Vision Missing", "No Vision Runner was found");
	}
	public void stop(){
		
	}
	public void updateParam(){
		if(Dashboard.getVision() == null || Dashboard.getVision().getProcessing() == null) return;
		VisionFilter[] filters = Dashboard.getVision().getProcessing().getFilters();
		for (VisionFilter filter : filters) {
			if(filter instanceof ColorFilter){
				colorFilter = (ColorFilter) filter;
				break;
			}
		}
		if(colorFilter == null) return;
		local = true;
		h_max.setValue(colorFilter.max1Property().get());  
		h_max_val.setText(String.valueOf(colorFilter.max1Property().get()));
		h_min.setValue(colorFilter.min1Property().get()); 
		h_min_val.setText(String.valueOf(colorFilter.min1Property().get()));
		s_max.setValue(colorFilter.max2Property().get());  
		s_max_val.setText(String.valueOf(colorFilter.max2Property().get()));
		s_min.setValue(colorFilter.min2Property().get());  
		s_min_val.setText(String.valueOf(colorFilter.min2Property().get()));
		v_max.setValue(colorFilter.max3Property().get());  
		v_max_val.setText(String.valueOf(colorFilter.max3Property().get()));
		v_min.setValue(colorFilter.min3Property().get());  
		v_min_val.setText(String.valueOf(colorFilter.min3Property().get()));
		if(hsv_check.isSelected() != colorFilter.hsvProperty().get()){
			hsv_check.setSelected(colorFilter.hsvProperty().get());
			setForHSV(colorFilter.hsvProperty().get());
		}
		local = false;
	}
	public void setParam(){
		if(Dashboard.getVision() == null) return;
		colorFilter = new ColorFilter();
		colorFilter.hsvProperty().set(hsv_check.isSelected());
		colorFilter.set((int) h_min.getValue(), (int)h_max.getValue(), 
				(int) s_min.getValue(), (int)s_max.getValue(), 
				(int) v_min.getValue(), (int)v_max.getValue());
		VisionProcessing proc = new VisionProcessing();
		proc.addFilter(colorFilter);
		Dashboard.getVision().addProcessing(proc);
	}
	public void updateVisionRun(){
		local = true;
		if(Dashboard.getVision() == null && vision_check.isSelected())
			vision_check.setSelected(false);
		else if(Dashboard.getVision() != null && vision_check.isSelected() != Dashboard.getVision().isRunning())
			vision_check.setSelected(Dashboard.getVision().isRunning());
		local = false;
	}
	public void setForHSV(boolean hsv){
		if(hsv){
			h_min_lbl.setText("Min Hue");
			h_max_lbl.setText("Max Hue");
			s_min_lbl.setText("Min Saturation");
			s_max_lbl.setText("Max Saturation");
			v_min_lbl.setText("Min Value");
			v_max_lbl.setText("Max Value");
			
			if(h_min.getValue() > 180) h_min.setValue(180);
			if(h_max.getValue() > 180) h_max.setValue(180);
			h_min.setMax(180);
			h_max.setMax(180);
		}else{
			h_min_lbl.setText("Min Red");
			h_max_lbl.setText("Max Red");
			s_min_lbl.setText("Min Green");
			s_max_lbl.setText("Max Green");
			v_min_lbl.setText("Min Blue");
			v_max_lbl.setText("Max Blue");
			h_min.setMax(255);
			h_max.setMax(255);
		}
		hsvSet = hsv;
	}
	
	
	public void addToManual(Node node){
		manual_controls.getChildren().add(node);
	}
	public void addToCameras(Node node){
		camera_node.getChildren().add(node);
	}
	public void addToData(Node node){
		sensor_node.getChildren().add(node);
	}
	public void addToControllers(Node node){
		controller_node.getChildren().add(node);
	}
	public void addToDisplay(DisplayType t, Node n){
		if(n == null) return;
		switch (t) {
		case Cam: addToCameras(n);
			break;
		case Controller: addToControllers(n);
			break;
		case Data: addToData(n);
			break;
		case Manual: addToManual(n);
			break;
		}
	}
	public void resetDisplay(){
		manual_controls.getChildren().clear();
		camera_node.getChildren().clear();
		sensor_node.getChildren().clear();
		controller_node.getChildren().clear();
		
		for (Enumeration<Displayble> denum = Dashboard.getDisplaybles(); denum.hasMoreElements();)
			denum.nextElement().reset();
		Dashboard.resetDisplaybles();
	}
	public void update(){
		for (Enumeration<Displayble> denum = Dashboard.getDisplaybles(); denum.hasMoreElements();) {
			Displayble d = denum.nextElement();
			d.update();
			if(!d.init()) 
				addToDisplay(d.getDisplayType(), d.setDisplay());
			Runnable r = d.updateDisplay();
			if(r != null) r.run();
		}
	}
}
