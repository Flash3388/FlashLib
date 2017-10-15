package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.flashboard.FlashboardHIDControl;
import edu.flash3388.flashlib.robot.PeriodicRunnable;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class HIDWindow extends Stage implements Runnable{
	
	private static final int BAR_WIDTH = 100;
	private static final int BAR_HEIGHT = 10;
	private static final int LABEL_WIDTH = 80;
	private static final int LABEL_HEIGHT = 10;
	private static final int ELLIPSE_RADIUS = 8;
	
	private static HIDWindow instance;
	private int selectedHID = -1;
	private ListView<String> controllerView;
	
	private VBox axesDataBox;
	private ProgressBar[] axesData;
	private Label[] axesLabels;
	
	private HBox buttonDataBox;
	private Ellipse[] buttonData;
	
	private Runnable updateRunnable;
	private Runnable task; 
	
	public HIDWindow() {
		setTitle("FLASHboard - HID");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	instance = null;
        	
        	if(task != null)
        		Dashboard.getUpdater().removeTask(task);
        	
        	Dashboard.getHIDControl().setEnabled(false);
        });
        
        updateRunnable = ()->update();
        task = new PeriodicRunnable(this, 50);
        Dashboard.getUpdater().addTask(task);
	}
	
	private void cancelSelection(){
		selectedHID = -1;
		
		axesDataBox.getChildren().clear();
		buttonDataBox.getChildren().clear();
		
		axesData = null;
		axesLabels = null;
		buttonData = null;
	}
	private void selectedHID(int index){
		if(!Dashboard.getHIDControl().isHIDConnected(index)){
			cancelSelection();
			return;
		}
		selectedHID = -1;
		
		axesDataBox.getChildren().clear();
		buttonDataBox.getChildren().clear();
		
		int count = Dashboard.getHIDControl().getHIDAxisCount(index);
		axesData = new ProgressBar[count];
		axesLabels = new Label[count];
		for (int i = 0; i < count; i++) {
			axesData[i] = new ProgressBar(0.5);
			axesData[i].setPrefSize(BAR_WIDTH, BAR_HEIGHT);
			axesLabels[i] = new Label(i + ": 0");
			axesLabels[i].setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
			
			VBox box = new VBox();
			box.setSpacing(5.0);
			box.getChildren().addAll(axesLabels[i], axesData[i]);
			axesDataBox.getChildren().add(box);
		}
		
		count = Dashboard.getHIDControl().getHIDButtonCount(index);
		buttonData = new Ellipse[count];
		VBox leftB = new VBox(), rightB = new VBox();
		leftB.setSpacing(5.0);
		rightB.setSpacing(5.0);
		for (int i = 0; i < count; i++) {
			buttonData[i] = new Ellipse();
			buttonData[i].setFill(Color.RED);
			buttonData[i].setRadiusX(ELLIPSE_RADIUS);
			buttonData[i].setRadiusY(ELLIPSE_RADIUS);
			
			if(i > count / 2)
				rightB.getChildren().add(buttonData[i]);
			else
				leftB.getChildren().add(buttonData[i]);
		}
		buttonDataBox.getChildren().addAll(leftB, rightB);
		
		selectedHID = index;
	}
	
	private Scene loadScene(){
		
		//left
		controllerView = new ListView<String>();
		controllerView.setPadding(new Insets(5.0));
		controllerView.setMaxWidth(200);
		controllerView.getSelectionModel().selectedIndexProperty().addListener((obs, o, n)->{
			if(n.intValue() < 0)
				cancelSelection();
			else
				selectedHID(n.intValue());
		});
		for (int i = 0; i < FlashboardHIDControl.MAX_PORT_COUNT; i++) {
			controllerView.getItems().add(i + " - not connected");
		}
		
		HBox controllersBox = new HBox();
		controllersBox.setPadding(new Insets(10.0));
		controllersBox.getChildren().add(controllerView);
		
		//right
		axesDataBox = new VBox();
		buttonDataBox = new HBox();
		buttonDataBox.setSpacing(5.0);
		
		HBox dataBox = new HBox();
		dataBox.setSpacing(10.0);
		dataBox.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
		dataBox.getChildren().addAll(axesDataBox, buttonDataBox);
		
		BorderPane root = new BorderPane();
		root.setLeft(controllersBox);
		root.setRight(dataBox);
		
		return new Scene(root, 400, 300);
	}
	
	private void update(){
		for (int i = 0; i < FlashboardHIDControl.MAX_PORT_COUNT; i++) {
			if(Dashboard.getHIDControl().isHIDConnected(i)){
				controllerView.getItems().set(i, i + " - " + Dashboard.getHIDControl().getHIDName(i));
			}else{
				controllerView.getItems().set(i, i + " - not connected");
				if(selectedHID == i)
					cancelSelection();
			}
		}
		
		if(selectedHID < 0)
			return;
		
		int hid = selectedHID;
		double data;
		boolean datab;
		
		for (int i = 0; i < axesData.length; i++) {
			data = Dashboard.getHIDControl().getHIDAxis(hid, i);
			axesData[i].setProgress(data * 0.5 + 0.5);
			axesLabels[i].setText(i + ": " + data);
		}
		for (int i = 0; i < buttonData.length; i++) {
			datab = Dashboard.getHIDControl().getHIDButton(hid, i);
			buttonData[i].setFill(datab? Color.GREEN : Color.RED);
		}
	}

	@Override
	public void run() {
		Platform.runLater(updateRunnable);
	}
	
	
	public static void showHIDWindow(){
		if(instance == null)
			instance = new HIDWindow();
		
		if(!instance.isShowing()){
			Dashboard.getHIDControl().setEnabled(true);
			instance.show();
		}
	}
}
