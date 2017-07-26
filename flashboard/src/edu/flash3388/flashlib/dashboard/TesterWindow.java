package edu.flash3388.flashlib.dashboard;

import java.util.Enumeration;

import edu.flash3388.flashlib.dashboard.controls.FlashboardTester;
import edu.flash3388.flashlib.dashboard.controls.FlashboardTesterMotor;
import edu.flash3388.flashlib.gui.FlashFxUtils;
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
	
	@SuppressWarnings("unchecked")
	private Scene loadScene(){
		VBox root = new VBox();
		
		TableView<FlashboardTesterMotor> motorsView = new TableView<FlashboardTesterMotor>();
		TableColumn<FlashboardTesterMotor, String> nameCol = new TableColumn<FlashboardTesterMotor, String>();
		nameCol.setText("Name");
		TableColumn<FlashboardTesterMotor, Boolean> brakeCol = new TableColumn<FlashboardTesterMotor, Boolean>();
		brakeCol.setText("Brake Mode");
		TableColumn<FlashboardTesterMotor, Double> speedCol = new TableColumn<FlashboardTesterMotor, Double>();
		speedCol.setText("Speed");
		TableColumn<FlashboardTesterMotor, Double> voltageCol = new TableColumn<FlashboardTesterMotor, Double>();
		voltageCol.setText("Voltage");
		TableColumn<FlashboardTesterMotor, Double> currentCol = new TableColumn<FlashboardTesterMotor, Double>();
		currentCol.setText("Current");
		
		nameCol.setCellValueFactory(new PropertyValueFactory<FlashboardTesterMotor, String>("name"));
		brakeCol.setCellValueFactory(new PropertyValueFactory<FlashboardTesterMotor, Boolean>("brakeMode"));
		speedCol.setCellValueFactory(new PropertyValueFactory<FlashboardTesterMotor, Double>("speed"));
		voltageCol.setCellValueFactory(new PropertyValueFactory<FlashboardTesterMotor, Double>("voltage"));
		currentCol.setCellValueFactory(new PropertyValueFactory<FlashboardTesterMotor, Double>("current"));
		
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
			Enumeration<FlashboardTesterMotor> motors = tester.getMotors();
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
			FlashFxUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Tester is already open!");
			return;
		}
		instance = new TesterWindow();
		instance.show();
	}
	public static void reset(){
		if(instance != null){
			instance.keysBox.getItems().clear();
			instance.keysBox.getItems().add("-- Choose Tester --");
			instance.keysBox.getSelectionModel().select(0);
			if(instance.tester != null)
				instance.tester.enable(false);
		}
	}
}
