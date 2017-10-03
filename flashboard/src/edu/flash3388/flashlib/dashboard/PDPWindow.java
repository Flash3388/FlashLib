package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.util.Enumeration;

import edu.flash3388.flashlib.dashboard.controls.PDPControl;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PDPWindow extends Stage{
	
	private static final String IMAGE_PATH = "data/res/pdp.png";
	
	private static PDPWindow instance = null;
	private ImageView pdpView;
	private Label volt, temp, tCurrent;
	private VBox channelsRight, channelsLeft;
	private ComboBox<String> pdpBox;
	
	private PDPControl selectedPDP;
	private boolean reset = false;
	private Label[] channelLabels;
	
	private PDPWindow(){
		setTitle("FLASHBoard - PDP");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((v)->{
        	deselect();
        });
	}
	
	private Scene loadScene(){
		File file = new File(IMAGE_PATH);
		Image image = new Image(file.toURI().toString());
		
		pdpView = new ImageView();
		pdpView.setImage(image);
		pdpView.setFitHeight(200);
		pdpView.setFitWidth(150);
		
		pdpBox = new ComboBox<String>();
		pdpBox.getItems().add("--Choose PDP--");
		for(Enumeration<PDPControl> pdpEnum = PDPControl.getBoards(); pdpEnum.hasMoreElements();)
			pdpBox.getItems().add(pdpEnum.nextElement().getName());
		pdpBox.getSelectionModel().select(0);
		pdpBox.getSelectionModel().selectedIndexProperty().addListener((obse, o, n)->{
			int nVal = n.intValue();
			if(nVal <= 0) deselect();
			else select(nVal-1);
		});
		
		volt = new Label("Voltage: ");
		tCurrent = new Label("Total Current: ");
		temp = new Label("Temperature: ");
		
		//top
		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		top.getChildren().add(pdpBox);
		top.setPadding(new Insets(5, 0, 5, 0));
		
		//center
		channelsRight = new VBox();
		channelsRight.setAlignment(Pos.CENTER_LEFT);
		channelsRight.setSpacing(5.0);
		channelsLeft = new VBox();
		channelsLeft.setAlignment(Pos.CENTER_RIGHT);
		channelsLeft.setSpacing(5.0);
		
		VBox imgbox = new VBox();
		imgbox.setAlignment(Pos.CENTER);
		//imgbox.setMaxSize(100, 200);
		imgbox.getChildren().add(pdpView);
		
		HBox databox = new HBox();
		databox.setAlignment(Pos.CENTER);
		databox.setSpacing(5.0);
		databox.getChildren().addAll(volt, tCurrent, temp);
		
		BorderPane center = new BorderPane();
		center.setRight(channelsRight);
		center.setLeft(channelsLeft);
		center.setCenter(imgbox);
		center.setTop(databox);
		
		BorderPane root = new BorderPane();
		root.setCenter(center);
		root.setTop(top);
		
		resetData();
		
		return new Scene(root, 450, 300);
	}
	
	public void select(int index){
		selectedPDP = PDPControl.get(index);
		selectedPDP.updateSend(true);
		reset = false;
		
		if(channelLabels == null || channelLabels.length < selectedPDP.getChannels()){
			channelsLeft.getChildren().clear();
			channelsRight.getChildren().clear();
			channelLabels = new Label[selectedPDP.getChannels()];
			for (int i = 0; i < channelLabels.length/2; i++) {
				Label label = new Label();
				channelsLeft.getChildren().add(label);
				channelLabels[i] = label;
			}
			for (int i = channelLabels.length-1; i >= channelLabels.length/2; i--) {
				Label label = new Label();
				channelsRight.getChildren().add(label);
				channelLabels[i] = label;
			}
		}
	}
	public void update(){
		if(selectedPDP == null){
			if(!reset) reset();
			return;
		}
			
		volt.setText("Voltage: "+selectedPDP.getAllVoltage()+" V");
		temp.setText("Temperature: "+selectedPDP.getTemperature()+" C");
		tCurrent.setText("Total Current: "+selectedPDP.getAllCurrent()+" A");
		for (int i = 0; i < channelLabels.length; i++) {
			Label label = channelLabels[i];
			label.setText("Current "+(i+1)+": "+selectedPDP.getCurrent(i));
		}
	}
	public void resetData(){
		volt.setText("Voltage: 0.0 V");
		temp.setText("Temperature: 0.0 C");
		tCurrent.setText("Total Current: 0.0 A");
		if(channelLabels != null){
			for (int i = 0; i < channelLabels.length; i++)
				channelLabels[i].setText("Current "+(i+1)+": 0.0 A");
		}
		
		reset = true;
	}
	public void resetTotal(){
		selectedPDP = null;
		resetData();
		pdpBox.getItems().clear();
		pdpBox.getItems().add("--Choose PDP--");
		pdpBox.getSelectionModel().select(0);
	}
	public void deselect(){
		if(selectedPDP != null){
			selectedPDP.disableSend();
			selectedPDP = null;
		}
		if(!reset)
			resetData();
	}
	

	public static void showPDP(){
		if(instance == null)
			instance = new PDPWindow();
		
		if(!instance.isShowing())
			instance.show();
	}
	public static PDPWindow getInstance(){
		return instance;
	}
	public static boolean onScreen(){
		return instance != null && instance.isShowing();
	}
	public static void reset(){
		if(instance != null)
			instance.resetTotal();
	}
}
