package edu.flash3388.flashlib.gui;

import java.util.ArrayList;

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
		
		public void remove(){
			switch(type){
				case Boolean: ConstantsHandler.removeBoolean(name);
					break;
				case Number: ConstantsHandler.removeNumber(name);
					break;
				case String: ConstantsHandler.removeString(name);
					break;
			}
		}
		public boolean setValue(String value){
			if(type == Type.Boolean){
				try {
					boolean b = Boolean.parseBoolean(value);
					ConstantsHandler.putBoolean(name, b);
					return true;
				} catch (NumberFormatException e) { return false;}
			}
			if(type == Type.Number){
				try {
					double d = Double.parseDouble(value);
					ConstantsHandler.putNumber(name, d);
					return true;
				} catch (NumberFormatException e) { return false;}
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
	
	private boolean newProp(Property prop, String value){
		return prop.setValue(value);
			
	}
	private String[] getKeys(){
		props = new ArrayList<Property>();
		ArrayList<String> keys = new ArrayList<String>();
		
		String[] pKeys = ConstantsHandler.getStringMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(pKeys[i], Property.Type.String));
		}
		pKeys = ConstantsHandler.getNumberMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(String.valueOf(pKeys[i]), 
					Property.Type.Number));
		}
		pKeys = ConstantsHandler.getBooleanMapNames();
		for (int i = 0; i < pKeys.length; i++){
			keys.add(pKeys[i]);
			props.add(new Property(String.valueOf(pKeys[i])
					, Property.Type.Boolean));
		}
		return keys.toArray(new String[0]);
	}
	
	private void loadPropertyViewer(){
		keysBox = new ComboBox<String>();
		keysBox.setPrefWidth(150);
		valField = new TextField();
		valField.setPrefWidth(150);
		final Button save = new Button("Save"), newProp = new Button("New"), 
				delete = new Button("Delete");
		save.setDisable(true);
		delete.setDisable(true);
		
		keysBox.getItems().add("");
		String[] keys = getKeys();
		keysBox.getItems().addAll(keys);
		keysBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue)->{
			local = true;
			if(newValue.intValue() != 0){
				cProp = props.get(newValue.intValue() - 1);
				valField.setText(cProp.getValue());
				delete.setDisable(false);
			}else {
				valField.setText("");
				delete.setDisable(true);
			}
			local = false;
			save.setDisable(true);
		});
		valField.textProperty().addListener((observable, oldValue, newValue)->{
			if(!local && keysBox.getSelectionModel().getSelectedIndex() > 0)
				save.setDisable(false);
		});
		
		delete.setOnAction((e)->{
			int i = keysBox.getSelectionModel().getSelectedIndex();
			if(i != 0){
				keysBox.getItems().remove(i);
				props.get(i - 1).remove();
				props.remove(i - 1);
			}
			keysBox.getSelectionModel().select(i - 1);
		});
		save.setOnAction((e)->{
			String newVal = valField.getText();
			if(!newProp(cProp, newVal))
				Dialog.show(this, "Error", "Value is incompatible with property type");
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
		buttonNode.getChildren().addAll(save, newProp, delete);
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
			System.out.println(t);
			System.out.println(newVal);
			
			cProp = new Property(keyName, t);
			if(!newProp(cProp, newVal))
				Dialog.show(this, "Error", "Value is incompatible with property type");
			else close();
		});
		cancel.setOnAction((e)->{
			cProp = null;
			close();
		});
		
		VBox viewerNode = new VBox();
		viewerNode.getChildren().addAll(nameField, valField, typeBox);
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
