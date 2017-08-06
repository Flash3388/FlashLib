package edu.flash3388.flashlib.dashboard;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import edu.flash3388.flashlib.dashboard.controls.PDP;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PDPWindowController implements Initializable{

	private static final String IMAGE_PATH = "data/res/pdp.png";
	
	@FXML ImageView pdpView;
	@FXML Label volt, temp, tCurrent;
	@FXML VBox channelsRight, channelsLeft;
	@FXML ComboBox<String> pdpBox;
	
	PDP selectedPDP;
	boolean reset = false;
	Label[] channelLabels;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		File file = new File(IMAGE_PATH);
		Image image = new Image(file.toURI().toString());
		pdpView.setImage(image);
		
		pdpBox.getItems().add("--Choose PDP--");
		for(Enumeration<PDP> pdpEnum = PDP.getBoards(); pdpEnum.hasMoreElements();)
			pdpBox.getItems().add(pdpEnum.nextElement().getName());
		pdpBox.getSelectionModel().select(0);
		pdpBox.valueProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue)->{
			int nVal = pdpBox.getSelectionModel().getSelectedIndex();
			if(nVal == 0) deselect();
			else select(nVal-1);
		});
	}

	public void select(int index){
		selectedPDP = PDP.get(index);
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
	public void reset(){
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
		reset();
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
			reset();
	}
}
