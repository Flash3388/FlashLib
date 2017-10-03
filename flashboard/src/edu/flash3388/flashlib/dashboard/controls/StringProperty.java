package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class StringProperty extends Displayable{

	private static final int LABEL_WIDTH = 150;
	private static final int LABEL_HEIGHT = 10;
	
	private String value = "";
	
	private Label label;
	private VBox node;
	private boolean changed = true;
	
	public StringProperty(String name) {
		super(name, FlashboardSendableType.STRING);
		node = new VBox();
		label = new Label(name + ": " + value);
		label.setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
		node.getChildren().add(label);
	}

	@Override
	protected void update() {
		if(changed){
			changed = false;
			label.setText(getName() + ": " + value);
		}
	}
	@Override
	protected Node getNode(){
		return node;
	}
	
	@Override
	public void newData(byte[] bytes) {
		synchronized(this){
			value = new String(bytes);
			changed = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		return null;
	}
	@Override
	public boolean hasChanged() {
		return false;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
}
