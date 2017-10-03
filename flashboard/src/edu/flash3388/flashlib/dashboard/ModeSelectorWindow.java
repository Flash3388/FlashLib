package edu.flash3388.flashlib.dashboard;

import java.util.Optional;

import edu.flash3388.flashlib.gui.FlashFXUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;

public class ModeSelectorWindow extends Stage{

	private static ModeSelectorWindow instance;
	
	private ToggleButton enabled;
	private ToggleButton disabled;
	
	private VBox statesBox;
	private ToggleGroup statesToggleGroup;
	private ToggleButton[] statesButtons;
	
	private ModeSelectorWindow(){
		setTitle("FLASHboard - Mode Selector");
		initStyle(StageStyle.DECORATED);
        initModality(Modality.NONE);
        setResizable(true);
        setScene(loadScene());
        setOnCloseRequest((e)->{
        	Dashboard.getModeSelectorControl().setDisabled(true);
        	instance = null;
        });
	}
	
	private void addState(){
		Dialog<Pair<String, Integer>> dialog = new Dialog<Pair<String, Integer>>();
		dialog.setTitle("New State");
		dialog.initOwner(this);
		
		ButtonType addType = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, addType);
		
		GridPane grid = new GridPane();
		grid.setHgap(10.0);
		grid.setVgap(10.0);
		grid.setPadding(new Insets(20.0, 150.0, 10.0, 10.0));
		
		TextField stateName = new TextField();
		stateName.setPromptText("State Name");
		TextField stateValue = new TextField();
		stateValue.setPromptText("State Value");
		stateValue.setText("1");
		
		grid.add(new Label("State Name:"), 0, 0);
		grid.add(stateName, 1, 0);
		grid.add(new Label("State Value:"), 0, 1);
		grid.add(stateValue, 1, 1);
		
		stateValue.textProperty().addListener((obs, o, n)->{
			try {
				Integer.parseInt(n);
			} catch (NumberFormatException e) {
				FlashFXUtils.showErrorDialog(this, "Value Error", "State Value is not valid");
				stateValue.setText(o);
				return;
			}
		});
		
		dialog.getDialogPane().setContent(grid);
		
		dialog.setResultConverter(dialogButton -> {
			if(dialogButton == addType){
				int value = Integer.parseInt(stateValue.getText());
				return new Pair<String, Integer>(stateName.getText(), value);
			}
			
			return null;
		});
		
		Optional<Pair<String, Integer>> result = dialog.showAndWait();
		
		if(!result.isPresent())
			return;
		
		String name = result.get().getKey();
		int value = result.get().getValue();
		
		if(Dashboard.getModeSelectorControl().getStateForName(name) != null){
			FlashFXUtils.showErrorDialog(this, "Name Taken", "Another state already uses the name: "+name);
			return;
		}
		if(Dashboard.getModeSelectorControl().getStateForValue(value) != null){
			FlashFXUtils.showErrorDialog(this, "Value Taken", "Another state already uses the value: "+value);
			return;
		}
		
		Dashboard.getModeSelectorControl().addState(name, value);
		loadStates();
	}
	private void removeState(){
		int selected = getSelectedState();
		
		if(selected < 0){
			FlashFXUtils.showErrorDialog(this, "Remove Error", "No state was selected");
			return; 
		}
		
		Dashboard.getModeSelectorControl().removeState(selected);
		loadStates();
	}
	private void loadStates(){
		statesBox.getChildren().clear();
		
		int statescount = Dashboard.getModeSelectorControl().getStatesCount();
		statesButtons = new ToggleButton[statescount];
		
		for (int i = 0; i < statesButtons.length; i++) {
			statesButtons[i] = new ToggleButton(
					Dashboard.getModeSelectorControl().getState(i).name);
			statesButtons[i].setMinSize(130, 20);
			statesButtons[i].setToggleGroup(statesToggleGroup);
			statesBox.getChildren().add(statesButtons[i]);
		}
		
		if(statescount > 0)
			statesButtons[0].setSelected(true);
	}
	private int getSelectedState(){
		ToggleButton selectedButton = (ToggleButton)statesToggleGroup.getSelectedToggle();
		if(selectedButton == null)
			return -1;
		
		int index = -1;
		for (int i = 0; i < statesButtons.length; i++) {
			if(selectedButton.equals(statesButtons[i])){
				index = i;
				break;
			}
		}
		
		return index;
	}
	private void setDisabled(boolean disabled){
		if(this.disabled.isSelected() != disabled)
			this.disabled.setSelected(disabled);
		if(Dashboard.getModeSelectorControl().isDisabled() != disabled)
			Dashboard.getModeSelectorControl().setDisabled(disabled);
	}
	
	private Scene loadScene(){
		ToggleGroup toggleGroup = new ToggleGroup();
		
		enabled = new ToggleButton("Enable");
		enabled.setToggleGroup(toggleGroup);
		enabled.setTextFill(Color.GREEN);
		enabled.setMinSize(120, 50);
		enabled.setOnAction((e)->{
			if(!Dashboard.getModeSelectorControl().remoteAttached()){
				e.consume();
				setDisabled(true);
				FlashFXUtils.showErrorDialog(this, "Error", "Remote mode selector not connected");
				return;
			}
			if(!Dashboard.getModeSelectorControl().isDisabled()){
				e.consume();
				enabled.setSelected(true);
				return;
			}
			
			int state = getSelectedState();
			if(state < 0){
				e.consume();
				setDisabled(true);
				FlashFXUtils.showErrorDialog(this, "Error", "No state was selected");
				return;
			}
			
			Dashboard.getModeSelectorControl().setCurrentState(state);
			Dashboard.getModeSelectorControl().setDisabled(false);
		});
		
		disabled = new ToggleButton("Disable");
		disabled.setToggleGroup(toggleGroup);
		disabled.setTextFill(Color.RED);
		disabled.setMinSize(120, 50);
		disabled.setOnAction((e)->{
			if(Dashboard.getModeSelectorControl().isDisabled()){
				e.consume();
				disabled.setSelected(true);
				return;
			}
			
			Dashboard.getModeSelectorControl().setDisabled(true);
		});
		disabled.setSelected(true);
		
		Dashboard.getModeSelectorControl().disabledProperty().addListener((obs, o, n)->{
			setDisabled(n.booleanValue());
		});
		
		statesToggleGroup = new ToggleGroup();
		statesToggleGroup.selectedToggleProperty().addListener((obs, o, n)->{
			if(!disabled.isSelected()){
				setDisabled(true);
			}
		});
		statesBox = new VBox();
		statesBox.setSpacing(5.0);
		loadStates();
		
		MenuItem addState = new MenuItem("Add");
		addState.setOnAction((e)->{
			addState();
		});
		MenuItem removeState = new MenuItem("Remove");
		removeState.setOnAction((e)->{
			removeState();
		});
		
		MenuBar toolbar = new MenuBar();
		Menu states = new Menu("States");
		states.getItems().addAll(addState, removeState);
		toolbar.getMenus().add(states);
		
		HBox statesb = new HBox();
		statesb.setSpacing(10.0);
		statesb.setAlignment(Pos.CENTER);
		statesb.setPadding(new Insets(10.0));
		statesb.getChildren().addAll(statesBox);
		
		HBox controlBox = new HBox();
		controlBox.setSpacing(5.0);
		controlBox.setAlignment(Pos.CENTER);
		controlBox.setPadding(new Insets(10.0));
		controlBox.getChildren().addAll(enabled, disabled);
		
		BorderPane root = new BorderPane();
		root.setTop(toolbar);
		root.setCenter(statesb);
		root.setBottom(controlBox);
		
		return new Scene(root, 300, 200);
	}
	
	public static void showModeSelector(){
		if(instance == null)
			instance = new ModeSelectorWindow();
		
		if(!instance.isShowing())
			instance.show();
	}
}
