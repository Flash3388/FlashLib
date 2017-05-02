package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import edu.flash3388.flashlib.dashboard.Displayble.DisplayType;
import edu.flash3388.flashlib.dashboard.controls.CameraViewer.DisplayMode;
import edu.flash3388.flashlib.dashboard.controls.Tester;
import edu.flash3388.flashlib.gui.Dialog;
import edu.flash3388.flashlib.gui.FileDialog;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import edu.flash3388.flashlib.gui.PropertyViewer;
import edu.flash3388.flashlib.gui.ShellWindow;
import edu.flash3388.flashlib.gui.ShellWindow.ChannelType;
import edu.flash3388.flashlib.dashboard.Remote.RemoteHost;
import edu.flash3388.flashlib.dashboard.Remote.User;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.vision.CvRunner;
import edu.flash3388.flashlib.vision.ProcessingParam;
import edu.flash3388.flashlib.vision.ProcessingParam.DetectingMode;
import edu.flash3388.flashlib.vision.Range;
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

public class MainController implements Initializable{

	private static class UpdateTask implements Runnable{
		MainController controller;
		boolean lastconnectionC = false, lastconnectionS = false;
		Runnable dxUpdate = ()->{
			controller.update(); 
			CvRunner vision = Dashboard.getVision();
			if(vision != null){
				if(vision.isLocalParameters() && vision.getParameters() == null)
					Dashboard.loadDefaultParameters();
				else 
					controller.updateParam();
				controller.updateVisionRun();
			}
			if(Dashboard.communicationsConnected() != lastconnectionC){
				lastconnectionC = !lastconnectionC;
				controller.connectionRect.setFill(lastconnectionC ? Color.GREEN : Color.RED);
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
	@FXML CheckBox hsv_check, morph_check, vision_check, boiler_check;
	@FXML ComboBox<String> mode_box;
	@FXML Rectangle camserverRect, connectionRect;
	@FXML VBox controller_node, sensor_node, camera_node;
	@FXML HBox manual_controls;
	@FXML ToolBar camera_toolbar;
	@FXML ChoiceBox<String> displayBoxType;
	@FXML MenuItem motorTester, show_log, load_params, showpdp, save_params, prop_viewer, sbc_update, sbc_load,
				   sbc_ssh, sbc_sftp, sbc_controller;
	
	private UpdateTask threadTask;
	private boolean local = false;
	private boolean hsvSet = false;
	private DetectingMode[] modesV = DetectingMode.values();
	
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
		
		for(DetectingMode m : modesV)
			mode_box.getItems().add(m.toString());
		mode_box.getSelectionModel().select(0);
		
		sbc_ssh.setOnAction((e)->{
			RemoteHost host = Remote.getRemoteHost("beaglebone-3388.local");
			User user = host.getUser("root");
			ShellWindow.showShellWindow(Dashboard.getPrimary(), ChannelType.SSH, host, user);
		});
		sbc_update.setOnAction((e)->{
			//TODO: IMPLEMENT
			//File folder = FileDialog.showDirectoryChooser(Dashboard.getPrimary());
		});
		load_params.setOnAction((e)->{
			if(Dashboard.getVision() == null){
				Dialog.show(Dashboard.getPrimary(), "Error", "Vision was not set!\nCannot load parameters");
				return;
			}
			File file = FileDialog.showLoadDialog(Dashboard.getPrimary(), Dashboard.FOLDER_SAVES, 
					Dashboard.EXT_VISION_PARAM);
			if(file == null) return;
			String path = file.getAbsolutePath();
			FlashUtil.getLog().log("Loading file: "+path);
			ProcessingParam p = ProcessingParam.loadFromFile(path);
			if(Dashboard.getVision().isLocalParameters() && p != null){
				FlashUtil.getLog().log("Parameters loaded");
				Dashboard.getVision().setParameters(p);
				updateParam();
			}else FlashUtil.getLog().log("Loading failed");
		});
		save_params.setOnAction((e)->{
			if(Dashboard.getVision() == null){
				Dialog.show(Dashboard.getPrimary(), "Error", "Vision was not set!\nCannot save parameters");
				return;
			}
			if(Dashboard.getVision().getParameters() != null){
				File file = FileDialog.showSaveDialog(Dashboard.getPrimary(), Dashboard.FOLDER_SAVES, "param",
						Dashboard.EXT_VISION_PARAM);
				if(file == null) return;
				String path = file.getAbsolutePath();
				FlashUtil.getLog().log("Saving parameters: "+path);
				Dashboard.getVision().getParameters().saveFile(path);
			}
		});
		motorTester.setOnAction((e)->showTester());
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
			if(!checkVision()) return;
			
			int newval = newValue.intValue();
			Dashboard.getVision().getParameters().mode = modesV[newval];
		});
		morph_check.setOnAction((e)->{
			if(!checkVision()) return;
			
			Dashboard.getVision().getParameters().morphOps = morph_check.isSelected();
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
			
			Dashboard.getVision().getParameters().hsv = hsv_check.isSelected();
			setForHSV(hsv_check.isSelected());
		});
		boiler_check.setOnAction((e)->{
			if(!checkVision()) return;
			
			Dashboard.getVision().getParameters().targetBoiler = boiler_check.isSelected();
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
				
				Dashboard.getVision().getParameters().hue_red.start = new_val.intValue();
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
				
				Dashboard.getVision().getParameters().hue_red.end = new_val.intValue();
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
				
				Dashboard.getVision().getParameters().sat_green.start = new_val.intValue();
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
				
				Dashboard.getVision().getParameters().sat_green.end = new_val.intValue();
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
				
				Dashboard.getVision().getParameters().val_blue.start = new_val.intValue();
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
				
				Dashboard.getVision().getParameters().val_blue.end = new_val.intValue();
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
	
	private boolean checkVision(){
		if(local) 
			return false;
		CvRunner vision = Dashboard.getVision();
		if(vision == null) 
			return false;
		if(vision.getParameters() == null && vision.isLocalParameters())
			setParam();
		if(!vision.isLocalParameters()){
			updateParam();
			return false;
		}
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
		PropertyViewer.showPropertyViewer(Dashboard.getPrimary(), Dashboard.getProperties().getMap());
	}
	private void showPDP(){
		PDPWindow.showPDP();
	}
	private void showLog(){
		LogWindow.showLog();
	}
	private void showTester(){
		if(Tester.getInstance() == null){
			
		}
		else TesterWindow.showTester();
	}
	public void stop(){
		
	}
	public void updateParam(){
		if(Dashboard.getVision() == null) return;
		ProcessingParam p = Dashboard.getVision().getParameters();
		if(p == null) return;
		local = true;
		h_max.setValue(p.hue_red.end);    h_max_val.setText(String.valueOf(p.hue_red.end));
		h_min.setValue(p.hue_red.start);  h_min_val.setText(String.valueOf(p.hue_red.start));
		s_max.setValue(p.sat_green.end);  s_max_val.setText(String.valueOf(p.sat_green.end));
		s_min.setValue(p.sat_green.start);s_min_val.setText(String.valueOf(p.sat_green.start));
		v_max.setValue(p.val_blue.end);   v_max_val.setText(String.valueOf(p.val_blue.end));
		v_min.setValue(p.val_blue.start); v_min_val.setText(String.valueOf(p.val_blue.start));
		if(hsv_check.isSelected() != p.hsv){
			hsv_check.setSelected(p.hsv);
			setForHSV(p.hsv);
		}
		if(morph_check.isSelected() != p.morphOps)
			morph_check.setSelected(p.morphOps);
		if(mode_box.getSelectionModel().getSelectedIndex() != p.mode.value)
			mode_box.getSelectionModel().select(p.mode.value);
		if(vision_check.isSelected() != Dashboard.getVision().isRunning())
			vision_check.setSelected(Dashboard.getVision().isRunning());
		if(boiler_check.isSelected() != p.targetBoiler)
			boiler_check.setSelected(p.targetBoiler);
		local = false;
	}
	public void setParam(){
		if(Dashboard.getVision() == null) return;
		ProcessingParam p = new ProcessingParam();
		p.hsv = hsv_check.isSelected();
		p.mode = modesV[mode_box.getSelectionModel().getSelectedIndex()];
		p.blur = 7;
		p.targetBoiler = true;
		p.morphOps = morph_check.isSelected();
		p.hue_red = new Range((int) h_min.getValue(), (int)h_max.getValue());
		p.sat_green = new Range((int) s_min.getValue(), (int)s_max.getValue());
		p.val_blue = new Range((int) v_min.getValue(), (int)v_max.getValue());
		Dashboard.getVision().setParameters(p);
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
