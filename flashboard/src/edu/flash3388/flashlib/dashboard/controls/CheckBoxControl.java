package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

public class CheckBoxControl extends Displayable{

	private CheckBox checkBox;
	private VBox root;
	
	private Object valueMutex = new Object();
	private boolean rChanged = false, lChanged = false, localChange = false;
	private boolean value = false;
	private byte[] send = new byte[1];
	
	public CheckBoxControl(String name) {
		super(name, FlashboardSendableType.CHECKBOX);
		
		checkBox = new CheckBox(name);
		checkBox.selectedProperty().addListener((obs, o, n)->{
			if(localChange)
				return;
			synchronized (valueMutex) {
				lChanged = true;
				value = n.booleanValue();
				send[0] = (byte) (value? 1: 0);
			}
		});
		
		root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.getChildren().add(checkBox);
	}

	@Override
	protected Node getNode() {
		return root;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Input;
	}
	@Override
	protected void update() {
		synchronized (valueMutex) {
			if(lChanged || rChanged){
				if(value != checkBox.isSelected()){
					localChange = true;
					checkBox.setSelected(value);
					localChange = false;
				}
			}	
		}
	}
	
	@Override
	public void newData(byte[] data) throws SendableException {
		synchronized (valueMutex) {
			rChanged = true;
			value = data[0] == 1;
		}
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		lChanged = false;
		return send;
	}
	@Override
	public boolean hasChanged() {
		return lChanged;
	}

	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
}
