package edu.flash3388.flashlib.gui;

import java.util.ArrayList;
import java.util.Map;

import edu.flash3388.flashlib.util.ConstantsHandler;
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

	private static class Property{
		private static enum Type{
			String, Boolean, Number
		}
		
		private String name;
		private Type type;
		
		public Property(String name, Type type){
			this.name = name;
			this.type = type;
		}
		
		public String getName(){
			return name;
		}
		public String getValue(){
			switch(type){
				case Boolean: 
					return String.valueOf(ConstantsHandler.getBooleanNative(name));
				case Number: 
					return String.valueOf(ConstantsHandler.getNumberNative(name));
				case String: 
					return ConstantsHandler.getStringNative(name);
				default: return "";
			}
		}
		
		public boolean setValue(String value){
			if(type == Type.Boolean){
				try {
					ConstantsHandler.putBoolean(name, Boolean.parseBoolean(value));
					return true;
				} catch (Exception e) { return false;}
			}
			if(type == Type.Number){
				try {
					ConstantsHandler.putNumber(name, Double.parseDouble(value));
					return true;
				} catch (Exception e) { return false;}
			}
			if(type == Type.String){
				ConstantsHandler.putString(name, value);
				return true;
			}
			return true;
		}
	}
	
	private ArrayList<Property> props;
	private TextField valField, nameField;
	private ComboBox<String> keysBox;
	private Property cProp;
	private boolean local = false;
	
	private PropertyViewer(Stage owner){
		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
		setResizable(false);
	}
	
	private void newProp(Property prop, String value){
		if(!prop.setValue(value))
			Dialog.show(this, "Error", "Value is incompatible with property type");
	}
	private String[] getKeys(){
		props = new ArrayList<Property>();
		ArrayList<String> keys = new ArrayList<String>();
		
		String[] pKeys = ConstantsHandler.getStringMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(ConstantsHandler.getStringNative(pKeys[i]), Property.Type.String));
		}
		pKeys = ConstantsHandler.getNumberMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(String.valueOf(ConstantsHandler.getNumberNative(pKeys[i])), 
					Property.Type.Number));
		}
		pKeys = ConstantsHandler.getBooleanMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(String.valueOf(ConstantsHandler.getBooleanNative(pKeys[i]))
					, Property.Type.Boolean));
		}
		return keys.toArray(new String[0]);
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
		keysBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			local = true;
			if(newValue.intValue() != 0){
				cProp = props.get(newValue.intValue());
				valField.setText(cProp.getValue());
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
			newProp(cProp, newVal);
			save.setDisable(true);
		});
		newProp.setOnAction((e)->{
			Property prop = PropertyViewer.showPropertyCreator(this);
			if(prop != null){
				props.add(prop);
				keysBox.getItems().add(prop.getName());
			}
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
		final ComboBox<Property.Type> typeBox = new ComboBox<Property.Type>();
		typeBox.setPrefWidth(150);
		typeBox.getItems().addAll(Property.Type.values());
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
			Property.Type t = typeBox.getValue();
			
			cProp = new Property(keyName, t);
			newProp(cProp, newVal);
			close();
		});
		cancel.setOnAction((e)->{
			cProp = null;
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
	
	public static void showPropertyViewer(Stage owner){
		PropertyViewer v = new PropertyViewer(owner);
		v.setTitle("Properties Viewer");
		v.loadPropertyViewer();
		v.showAndWait();
	}
	private static Property showPropertyCreator(Stage owner){
		PropertyViewer v = new PropertyViewer(owner);
		v.setTitle("Property Creator");
		v.loadPropertyCreator();
		v.showAndWait();
		return v.cProp;
	}
}
