package edu.flash3388.flashlib.dashboard;

import java.util.Enumeration;

import edu.flash3388.flashlib.dashboard.controls.FlashboardTester;
import edu.flash3388.flashlib.dashboard.controls.TesterMotorControl;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class TesterWindow extends Stage{

	private static TesterWindow instance;
	private FlashboardTester tester;
	private ComboBox<String> keysBox;
	
	private TesterWindow(){
		setTitle("FLASHboard - Tester");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(false);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	instance = null;
        });
	}
	
	private void refresh(){
		keysBox.getItems().clear();
		keysBox.getItems().add("-- Choose Tester --");
		keysBox.getSelectionModel().select(0);
	}
	@SuppressWarnings("unchecked")
	private Scene loadScene(){
		VBox root = new VBox();
		
		TableView<TesterMotorControl> motorsView = new TableView<TesterMotorControl>();
		TableColumn<TesterMotorControl, String> nameCol = new TableColumn<TesterMotorControl, String>();
		nameCol.setText("Name");
		TableColumn<TesterMotorControl, Boolean> brakeCol = new TableColumn<TesterMotorControl, Boolean>();
		brakeCol.setText("Brake Mode");
		TableColumn<TesterMotorControl, Double> speedCol = new TableColumn<TesterMotorControl, Double>();
		speedCol.setText("Speed");
		TableColumn<TesterMotorControl, Double> voltageCol = new TableColumn<TesterMotorControl, Double>();
		voltageCol.setText("Voltage");
		TableColumn<TesterMotorControl, Double> currentCol = new TableColumn<TesterMotorControl, Double>();
		currentCol.setText("Current");
		
		nameCol.setCellValueFactory(new PropertyValueFactory<TesterMotorControl, String>("name"));
		brakeCol.setCellValueFactory(new PropertyValueFactory<TesterMotorControl, Boolean>("brakeMode"));
		speedCol.setCellValueFactory(new PropertyValueFactory<TesterMotorControl, Double>("speed"));
		voltageCol.setCellValueFactory(new PropertyValueFactory<TesterMotorControl, Double>("voltage"));
		currentCol.setCellValueFactory(new PropertyValueFactory<TesterMotorControl, Double>("current"));
		
		motorsView.getColumns().addAll(nameCol, brakeCol, speedCol, voltageCol, currentCol);
		
		keysBox = new ComboBox<String>();
		VBox.setMargin(keysBox, new Insets(5.0, 0.0, 0.0, 0.0));
		keysBox.getItems().add("-- Choose Tester --");
		keysBox.getSelectionModel().select(0);
		keysBox.getItems().addAll(FlashboardTester.getTestersNames());
		keysBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue)->{
			motorsView.getItems().clear();
			if(tester != null){
				tester.enable(false);
				tester = null;
			}
			if(keysBox.getSelectionModel().selectedIndexProperty().get() == 0){
				return;
			}
			
			tester = FlashboardTester.getTester(newValue);
			if(tester == null){
				keysBox.getSelectionModel().select(0);
				return;
			}
			Enumeration<TesterMotorControl> motors = tester.getMotors();
			for(; motors.hasMoreElements();)
				motorsView.getItems().add(motors.nextElement());
			tester.enable(true);
		});
		
		
		root.setSpacing(10);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(keysBox, motorsView);
		return new Scene(root, 400, 245);
	}
	
	public static void showTester(){
		if(instance != null){
			FlashFXUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Tester is already open!");
			return;
		}
		instance = new TesterWindow();
		instance.show();
	}
	public static void resetTester(){
		if(instance != null){
			if(instance.tester != null)
				instance.tester.enable(false);
			instance.refresh();
		}
	}
}
