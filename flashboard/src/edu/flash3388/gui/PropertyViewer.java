package edu.flash3388.gui;

import java.util.Map;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PropertyViewer extends Stage{

	private Map<String, String> properties;
	private TextField valField, nameField;
	private ComboBox<String> keysBox;
	private String keyName;
	private boolean local = false;
	private String retVal = null;
	
	private PropertyViewer(Stage owner, Map<String, String> props){
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
		properties = props;
	}
	
	private void newProp(String prop, String value){
		properties.put(prop, value);
	}
	private String[] getKeys(){
		return properties.keySet().toArray(new String[0]);
	}
	
	private void loadPropertyViewer(){
		keysBox = new ComboBox<String>();
		keysBox.setPrefWidth(150);
		valField = new TextField();
		valField.setPrefWidth(150);
		final Button save = new Button("Save"), newProp = new Button("New");
		save.setDisable(true);
		
		keysBox.getItems().add("");
		String[] keys = getKeys();
		keysBox.getItems().addAll(keys);
		keysBox.valueProperty().addListener((observable, oldValue, newValue)->{
			local = true;
			if(!newValue.equals("")){
				keyName = newValue;
				valField.setText(properties.get(newValue));
			}else valField.setText("");
			local = false;
			save.setDisable(true);
		});
		valField.textProperty().addListener((observable, oldValue, newValue)->{
			if(!local && keysBox.getSelectionModel().getSelectedIndex() > 0)
				save.setDisable(false);
		});
		
		save.setOnAction((e)->{
			String newVal = valField.getText();
			newProp(keyName, newVal);
			save.setDisable(true);
		});
		newProp.setOnAction((e)->{
			String prop = PropertyViewer.showPropertyCreator(this, this.properties);
			if(prop != null)
				keysBox.getItems().add(prop);
		});
		VBox viewerNode = new VBox();
		viewerNode.getChildren().addAll(keysBox, valField);
		viewerNode.setSpacing(10);
		viewerNode.setAlignment(Pos.CENTER);
		viewerNode.setPadding(new Insets(10, 10, 10, 10));
		HBox buttonNode = new HBox();
		buttonNode.getChildren().addAll(save, newProp);
		buttonNode.setSpacing(10);
		buttonNode.setAlignment(Pos.CENTER_RIGHT);
		buttonNode.setPadding(new Insets(0, 5, 5, 0));
		BorderPane pane = new BorderPane();
		pane.setBottom(buttonNode);
		pane.setCenter(viewerNode);
		setScene(new Scene(pane, 200, 200));
	}
	private void loadPropertyCreator(){
		nameField = new TextField();
		nameField.setPrefWidth(150);
		valField = new TextField();
		valField.setPrefWidth(150);
		valField.setDisable(true);
		final Button save = new Button("Save"), cancel = new Button("Cancel");
		save.setDisable(true);
		
		nameField.textProperty().addListener((observable, oldValue, newValue)->{
			if(valField.isDisabled() && !newValue.equals(""))
				valField.setDisable(false);
		});
		valField.textProperty().addListener((observable, oldValue, newValue)->{
			if(!valField.getText().equals(""))
				save.setDisable(false);
			else save.setDisable(true);
		});
		
		save.setOnAction((e)->{
			String newVal = valField.getText();
			String keyName = nameField.getText();
			newProp(keyName, newVal);
			retVal = keyName;
			close();
		});
		cancel.setOnAction((e)->{
			retVal = null;
			close();
		});
		
		VBox viewerNode = new VBox();
		viewerNode.getChildren().addAll(nameField, valField);
		viewerNode.setSpacing(10);
		viewerNode.setAlignment(Pos.CENTER);
		viewerNode.setPadding(new Insets(10, 10, 10, 10));
		HBox buttonNode = new HBox();
		buttonNode.getChildren().addAll(save, cancel);
		buttonNode.setSpacing(10);
		buttonNode.setAlignment(Pos.CENTER_RIGHT);
		buttonNode.setPadding(new Insets(0, 5, 5, 0));
		BorderPane pane = new BorderPane();
		pane.setBottom(buttonNode);
		pane.setCenter(viewerNode);
		setScene(new Scene(pane, 200, 200));
	}
	
	public static void showPropertyViewer(Stage owner, Map<String, String> prop){
		PropertyViewer v = new PropertyViewer(owner, prop);
		v.setTitle("Properties Viewer");
		v.loadPropertyViewer();
		v.showAndWait();
	}
	public static String showPropertyCreator(Stage owner, Map<String, String> prop){
		PropertyViewer v = new PropertyViewer(owner, prop);
		v.setTitle("Property Creator");
		v.loadPropertyCreator();
		v.showAndWait();
		return v.retVal;
	}
}
