package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Dashboard;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.ValueType;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InputField extends Displayable{

	private static final int LABEL_WIDTH = 50;
	private static final int LABEL_HEIGHT = 10;
	
	private String value = "";
	private boolean changed = false;
	private ValueType type = null;
	
	private Label label;
	private TextField field;
	private javafx.scene.control.Button button;
	private VBox node;
	
	public InputField(String name) {
		super(name, FlashboardSendableType.INPUT);
		
		node = new VBox();
		label = new Label(name);
		field = new TextField();
		field.setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
		button = new javafx.scene.control.Button();
		button.setPrefSize(LABEL_WIDTH / 3, LABEL_HEIGHT);
		HBox d = new HBox();
		d.getChildren().addAll(field, button);
		node.getChildren().addAll(label, d);
		
		button.setOnAction((ActionEvent e)->{
			receiveInput();
		});
		field.setOnKeyPressed((KeyEvent e)->{
			if(e.getCode() == KeyCode.ENTER){
				receiveInput();
			}
		});
		field.focusedProperty().addListener((obs, o, n)->{
			if(o.booleanValue() && !n.booleanValue())
				receiveInput();
		});
	}

	private void receiveInput(){
		if(type == null){
			FlashFXUtils.showErrorDialog(Dashboard.getPrimary(), "Error", "Cannot edit input yet");
			return;
		}
		
		String val = field.getText();
		if(!validate(val)){
			return;
		}
		value = val;
		changed = true;
	}
	
	private boolean validate(String str){
		if(type == ValueType.Boolean){
			try {
				Boolean.parseBoolean(value);
				return true;
			} catch (NumberFormatException e) { return false;}
		}
		if(type == ValueType.Double){
			try {
				Double.parseDouble(value);
				return true;
			} catch (NumberFormatException e) { return false;}
		}
		if(type == ValueType.String){
			return true;
		}
		return false;
	}
	
	@Override
	protected Node getNode(){
		return node;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Manual;
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(bytes[0] == 0){
			value = new String(bytes, 1, bytes.length - 1);
			FlashFXUtils.onFXThread(()->{
				field.setText(value);
			});
		}else if(bytes[0] == 1){
			type = ValueType.values()[bytes[1]];
		}
	}
	@Override
	public byte[] dataForTransmition() {
		if(changed){
			changed = false;
			return value.getBytes();
		}
		return null;
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
}
