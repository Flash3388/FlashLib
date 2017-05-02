package edu.flash3388.flashlib.dashboard;

import java.net.URL;
import java.util.Enumeration;
import java.util.ResourceBundle;

import edu.flash3388.dashboard.controls.Tester;
import edu.flash3388.dashboard.controls.Tester.TesterMotor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TesterWindowController implements Initializable{
	
	@FXML VBox container;
	@FXML MenuItem close, currentDrawTest, functionalityTest, batteryTest;
	@FXML TableView<TesterMotor> table;
	@FXML TableColumn<TesterMotor, Integer> channel;
	@FXML TableColumn<TesterMotor, Boolean> enabled, type;
	@FXML TableColumn<TesterMotor, Double> current, voltage, speed;
	
	private Stage stage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		close.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});
		
		channel.setCellValueFactory(new PropertyValueFactory<TesterMotor, Integer>("channel"));
		enabled.setCellValueFactory(new PropertyValueFactory<TesterMotor, Boolean>("enabled"));
		type.setCellValueFactory(new PropertyValueFactory<TesterMotor, Boolean>("brakeMode"));
		speed.setCellValueFactory(new PropertyValueFactory<TesterMotor, Double>("speed"));
		voltage.setCellValueFactory(new PropertyValueFactory<TesterMotor, Double>("voltage"));
		current.setCellValueFactory(new PropertyValueFactory<TesterMotor, Double>("current"));
		
		Enumeration<TesterMotor> motors = Tester.getInstance().getMotors();
		for(; motors.hasMoreElements();){
			table.getItems().add(motors.nextElement());
		}
	}
	
	private void close(){
		stage.close();
	}
	public void setWindow(Stage st){
		this.stage = st;
	}
}
