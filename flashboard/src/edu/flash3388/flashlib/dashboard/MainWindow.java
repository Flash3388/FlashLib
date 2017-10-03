package edu.flash3388.flashlib.dashboard;

import java.io.File;

import edu.flash3388.flashlib.robot.PeriodicRunnable;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.VisionFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainWindow {

	//--------------------------------------------------------------------
	//-----------------------Classes--------------------------------------
	//--------------------------------------------------------------------
	
	private static class VisionControl{
		
		private Pane root;
		
		private Slider slider;
		private TextField inputText;
		private Label nameLabel;
		
		private ChangeListener<Number> listener;
		
		public VisionControl() {
			slider = new Slider();
			slider.setMin(0.0);
			slider.setMax(255.0);
			slider.setValue(0.0);
			slider.setMaxWidth(250.0);
			slider.setDisable(true);
			
			inputText = new TextField();
			inputText.setText("0");
			inputText.setMaxWidth(50.0);
			inputText.setOnKeyPressed((e)->{
				if(e.getCode() == KeyCode.ENTER){
					setTextFromField();
				}
			});
			inputText.focusedProperty().addListener((obs, o, n)->{
				if(!n.booleanValue()){
					inputText.setText(String.valueOf(slider.getValue()));
				}
			});
			
			nameLabel = new Label("");
			
			HBox top = new HBox();
			top.setSpacing(5.0);
			top.getChildren().addAll(nameLabel, inputText);
			
			VBox all = new VBox();
			all.setSpacing(10.0);
			all.getChildren().addAll(top, slider);
			root = all;
		}
		
		private void setTextFromField(){
			String text = inputText.getText();
			try{
				int value = Integer.parseInt(text);
				if(value < 0 || value > slider.getMax()){
					GUI.showMainErrorDialog("Invalid color value: "+text);
					inputText.setText(String.valueOf((int)slider.getValue()));
				}else{
					slider.setValue(value);
				}
			}catch(NumberFormatException e){
				GUI.showMainErrorDialog("Invalid color value: "+text);
			}
		}
		
		public Pane getRoot(){
			return root;
		}
		
		public void setName(String name){
			nameLabel.setText(name+":");
		}
		public void setMax(int max){
			slider.setMax(max);
			inputText.setText(String.valueOf((int)slider.getValue()));
		}
		public void setValue(int value){
			slider.setValue(value);
			inputText.setText(String.valueOf(value));
		}
		public int getValue(){
			return (int) slider.getValue();
		}
		
		public void bind(IntegerProperty prop){
			listener = (obs, o, n)->{
				prop.set(n.intValue());
			};
			slider.valueProperty().addListener(listener);
		}
		public void unbind(){
			if(listener != null){
				slider.valueProperty().removeListener(listener);
				listener = null;
			}
		}
		
		public void disable(){
			slider.setDisable(true);
			inputText.setDisable(true);
		}
		public void enable(){
			slider.setDisable(false);
			inputText.setDisable(false);
		}
	}
	
	//--------------------------------------------------------------------
	//-----------------------Vision---------------------------------------
	//--------------------------------------------------------------------
	
	private VisionControl vision_min1, vision_max1, vision_min2, vision_max2, 
			vision_min3, vision_max3;
	private ComboBox<String> vision_paramBox;
	private CheckBox vision_hsvBox, vision_runBox;
	private ColorFilter colorFilter;
	private boolean localVisionChange = false;
	
	private void refreshVisionParameters(){
		Object selected = vision_paramBox.getSelectionModel().getSelectedItem();
		vision_paramBox.getItems().clear();
		
		VisionProcessing processing = null;
		int selectedIndex = 0;
		for (int i = 0; i < Dashboard.getVision().getProcessingCount(); i++) {
			processing = Dashboard.getVision().getProcessing(i);
			vision_paramBox.getItems().add(processing.getName());
			
			if(processing.getName().equals(selected)){
				selectedIndex = i;
			}
		}
		
		vision_paramBox.getSelectionModel().select(selectedIndex);
	}
	private void selectParameters(int index){
		if(Dashboard.getVision().getSelectedProcessingIndex() != index){
			Dashboard.getVision().selectProcessing(index);
		}
		if(vision_paramBox.getSelectionModel().getSelectedIndex() != index){
			localVisionChange = true;
			vision_paramBox.getSelectionModel().select(index);
			localVisionChange = false;
		}
		
		
		VisionProcessing proc = Dashboard.getVision().getProcessing();
		
		colorFilter = null;
		VisionFilter[] filters = proc.getFilters();
		for (VisionFilter visionFilter : filters) {
			if(visionFilter instanceof ColorFilter){
				colorFilter = (ColorFilter) visionFilter;
				break;
			}
		}
		
		resetVisionColorControls();
		
		if(colorFilter != null){
			vision_min1.bind(colorFilter.min1Property());
			vision_min1.enable();
			vision_max1.bind(colorFilter.max1Property());
			vision_max1.enable();
			vision_min2.bind(colorFilter.min2Property());
			vision_min2.enable();
			vision_max2.bind(colorFilter.max2Property());
			vision_max2.enable();
			vision_min3.bind(colorFilter.min3Property());
			vision_min3.enable();
			vision_max3.bind(colorFilter.max3Property());
			vision_max3.enable();
			
			localVisionChange = true;
			vision_hsvBox.setDisable(false);
			vision_hsvBox.setSelected(colorFilter.hsvProperty().get());
			localVisionChange = false;
		}
	}
	private void loadVisionParameters(){
		File file = GUI.showVisionLoadDialog();
		
		if(file != null){
			VisionProcessing processing = null;
			
			try{
				processing = VisionProcessing.createFromXml(file.getAbsolutePath());
			}catch(Throwable t){
				FlashUtil.getLog().reportError(t);
				GUI.showMainErrorDialog("Failed to load vision");
			}
			
			if(processing != null){
				Dashboard.getVision().addProcessing(processing);
				refreshVisionParameters();
				selectParameters(Dashboard.getVision().getProcessingCount() - 1);
			}
		}
	}
	private void saveVisionParameters(){
		VisionProcessing processing = Dashboard.getVision().getProcessing();
		
		if(processing == null){
			GUI.showMainErrorDialog("No vision processing is selected");
		}else{
			File file = GUI.showVisionSaveDialog();
			
			if(file != null){
				processing.saveXml(file.getAbsolutePath());
			}
		}
	}
	private void changeVisionColorScheme(boolean hsv){
		if(hsv){
			vision_max1.setMax(180);
			
			vision_min1.setName("Min Hue");
			vision_max1.setName("Max Hue");
			vision_min2.setName("Min Saturation");
			vision_max2.setName("Max Saturation");
			vision_min3.setName("Min Value");
			vision_max3.setName("Max Value");
		}else{
			vision_max1.setMax(255);
			
			vision_min1.setName("Min Red");
			vision_max1.setName("Max Red");
			vision_min2.setName("Min Green");
			vision_max2.setName("Max Green");
			vision_min3.setName("Min Blue");
			vision_max3.setName("Max Blue");
		}
		
		if(vision_hsvBox.isSelected() != hsv){
			localVisionChange = true;
			vision_hsvBox.setSelected(hsv);
			localVisionChange = false;
		}
	}
	private void resetVisionColorControls(){
		colorFilter = null;
		
		vision_min1.unbind();
		vision_min1.disable();
		vision_max1.unbind();
		vision_max1.disable();
		vision_min2.unbind();
		vision_min2.disable();
		vision_max2.unbind();
		vision_max2.disable();
		vision_min3.unbind();
		vision_min3.disable();
		vision_max3.unbind();
		vision_max3.disable();
		
		changeVisionColorScheme(false);
		
		vision_min1.setValue(0);
		vision_min2.setValue(0);
		vision_min3.setValue(0);
		vision_max1.setValue(255);
		vision_max3.setValue(255);
		vision_max3.setValue(255);
		
		vision_hsvBox.setDisable(true);
	}
	private void updateVisionControls(){
		if(Dashboard.getVision().isRunning() != vision_runBox.isSelected()){
			localVisionChange = true;
			vision_runBox.setSelected(Dashboard.getVision().isRunning());
			localVisionChange = false;
		}
		if(Dashboard.getVision().getProcessingCount() != vision_paramBox.getItems().size()){
			refreshVisionParameters();
		}
		if(Dashboard.getVision().getSelectedProcessingIndex() != 
				vision_paramBox.getSelectionModel().getSelectedIndex()){
			selectParameters(Dashboard.getVision().getSelectedProcessingIndex());
		}
		if(colorFilter != null){
			if(colorFilter.hsvProperty().get() != vision_hsvBox.isSelected()){
				localVisionChange = true;
				vision_hsvBox.setSelected(colorFilter.hsvProperty().get());
				localVisionChange = false;
				changeVisionColorScheme(colorFilter.hsvProperty().get());
			}
			
			if(colorFilter.min1Property().get() != vision_min1.getValue()){
				vision_min1.setValue(colorFilter.min1Property().get());
			}
			if(colorFilter.max1Property().get() != vision_max1.getValue()){
				vision_max1.setValue(colorFilter.max1Property().get());
			}
			
			if(colorFilter.min2Property().get() != vision_min2.getValue()){
				vision_min2.setValue(colorFilter.min2Property().get());
			}
			if(colorFilter.max2Property().get() != vision_max2.getValue()){
				vision_max2.setValue(colorFilter.max2Property().get());
			}
			
			if(colorFilter.min3Property().get() != vision_min3.getValue()){
				vision_min3.setValue(colorFilter.min3Property().get());
			}
			if(colorFilter.max3Property().get() != vision_max3.getValue()){
				vision_max3.setValue(colorFilter.max3Property().get());
			}
		}
	}
	
	private Node initializeVisionControl(){
		vision_min1 = new VisionControl();
		vision_max1 = new VisionControl();
		vision_min2 = new VisionControl();
		vision_max2 = new VisionControl();
		vision_min3 = new VisionControl();
		vision_max3 = new VisionControl();
		
		vision_paramBox = new ComboBox<String>();
		vision_paramBox.setMaxWidth(100.0);
		refreshVisionParameters();
		vision_paramBox.getSelectionModel().selectedIndexProperty().addListener((obs, o, n)->{
			if(localVisionChange)
				return;
			selectParameters(n.intValue());
		});
		
		VBox paramBox = new VBox();
		paramBox.setSpacing(5.0);
		paramBox.setAlignment(Pos.CENTER);
		paramBox.getChildren().addAll(new Label("Parameters:"), vision_paramBox);
		
		vision_hsvBox = new CheckBox();
		vision_hsvBox.selectedProperty().addListener((obs, o, n)->{
			if(localVisionChange)
				return;
			
			if(colorFilter == null){
				GUI.showMainErrorDialog("No ColorFilter exists for vision");
				localVisionChange = true;
				vision_hsvBox.setSelected(true);
				localVisionChange = false;
				
				return;
			}
			
			changeVisionColorScheme(n.booleanValue());
		});
		resetVisionColorControls();
		changeVisionColorScheme(false);
		localVisionChange = true;
		vision_hsvBox.setSelected(false);
		localVisionChange = false;
		
		vision_runBox = new CheckBox();
		vision_runBox.selectedProperty().addListener((obs, o, n)->{
			if(localVisionChange)
				return;
			if(Dashboard.getVision().getProcessing() == null){
				GUI.showMainErrorDialog("Cannot run vision: not processing selected");
				localVisionChange = true;
				vision_runBox.setSelected(false);
				localVisionChange = false;
				return;
			}
			
			if(n.booleanValue())
				Dashboard.getVision().start();
			else
				Dashboard.getVision().stop();
		});
		vision_runBox.setSelected(false);
		
		HBox hsvBox = new HBox();
		hsvBox.setSpacing(5.0);
		//hsvBox.setAlignment(Pos.CENTER);
		hsvBox.getChildren().addAll(vision_hsvBox, new Label("HSV"));
		HBox runBox = new HBox();
		runBox.setSpacing(5.0);
		//runBox.setAlignment(Pos.CENTER);
		runBox.getChildren().addAll(vision_runBox, new Label("Vision"));
		
		VBox checkBox = new VBox();
		checkBox.setSpacing(10.0);
		checkBox.setAlignment(Pos.CENTER_LEFT);
		checkBox.getChildren().addAll(hsvBox, runBox);
		
		VBox visionBox = new VBox();
		visionBox.setSpacing(10.0);
		visionBox.setPadding(new Insets(20.0));
		//visionBox.setAlignment(Pos.CENTER);
		visionBox.getChildren().addAll(
				vision_min1.getRoot(), vision_max1.getRoot(),
				vision_min2.getRoot(), vision_max2.getRoot(),
				vision_min3.getRoot(), vision_max3.getRoot(),
				checkBox, paramBox);
		
		return visionBox;
	}
	
	//--------------------------------------------------------------------
	//-----------------------Toolbar--------------------------------------
	//--------------------------------------------------------------------
	
	private Node initializeToolbar(){
		Menu vision = new Menu("Vision");
		MenuItem loadvision = new MenuItem("Load Vision Parameters");
		loadvision.setOnAction((e)->{
			loadVisionParameters();
		});
		MenuItem savevision = new MenuItem("Save Vision Parameters");
		savevision.setOnAction((e)->{
			saveVisionParameters();
		});
		vision.getItems().addAll(loadvision, savevision);
		
		Menu monitoring = new Menu("Monitoring");
		MenuItem pdpwindow = new MenuItem("Show PDP");
		pdpwindow.setOnAction((e)->{
			GUI.showPDPWindow();
		});
		MenuItem testerwindow = new MenuItem("Show Motor Tester");
		testerwindow.setOnAction((e)->{
			GUI.showMotorTester();
		});
		monitoring.getItems().addAll(pdpwindow, testerwindow);
		
		Menu tools = new Menu("Tools");
		MenuItem hidwindow = new MenuItem("Show HID Control");
		hidwindow.setOnAction((e)->{
			GUI.showHIDControl();
		});
		MenuItem modeselector = new MenuItem("Show Mode Selector");
		modeselector.setOnAction((e)->{
			GUI.showModeSelector();
		});
		tools.getItems().addAll(hidwindow, modeselector);
		
		Menu help = new Menu("Help");
		MenuItem propviewer = new MenuItem("Show Properties");
		propviewer.setOnAction((e)->{
			GUI.showPropertiesViewer();
		});
		MenuItem commrestart = new MenuItem("Restart Communications");
		commrestart.setOnAction((e)->{
			Dashboard.restartCommunications();
		});
		help.getItems().addAll(propviewer, commrestart);
		
		MenuBar menubar = new MenuBar();
		menubar.getMenus().addAll(vision, monitoring, tools, help);
		return menubar;
	}
	
	//--------------------------------------------------------------------
	//-----------------------Center---------------------------------------
	//--------------------------------------------------------------------
	
	private ComboBox<String> cam_outputImageType;
	
	private Node initializeCenterNode(){
		
		
		VBox root = new VBox();
		return root;
	}
	
	//--------------------------------------------------------------------
	//-----------------------Controls-------------------------------------
	//--------------------------------------------------------------------
	
	private Node initializeDisplayControls(){
		
		HBox root = new HBox();
		return root;
	}
	
	private Node initializeInputControls(){
		
		BorderPane root = new BorderPane();
		return root;
	}
	
	//--------------------------------------------------------------------
	//-------------------------Main---------------------------------------
	//--------------------------------------------------------------------
	
	private static final int UPDATE_PERIOD = 100;
	
	private Runnable updateTask;
	
	public MainWindow() {
		updateTask = new PeriodicRunnable(()->{
			update();
		}, UPDATE_PERIOD);
		
		Dashboard.getUpdater().addTask(updateTask);
	}
	
	private void update(){
		updateVisionControls();
	}
	
	public Parent initializeMainScene(){
		Node toolBarNode = initializeToolbar();
		Node visionNode = initializeVisionControl();
		Node centerNode = initializeCenterNode();
		Node displayNode = initializeDisplayControls();
		Node inputNode = initializeInputControls();
		
		BorderPane root = new BorderPane();
		root.setTop(toolBarNode);
		root.setBottom(inputNode);
		root.setLeft(displayNode);
		root.setCenter(centerNode);
		root.setRight(visionNode);
		
		return root;
	}
}
