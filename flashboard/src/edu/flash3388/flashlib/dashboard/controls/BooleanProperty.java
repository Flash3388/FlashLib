package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class BooleanProperty extends Displayable{

	private static final int LABEL_WIDTH = 150;
	private static final int LABEL_HEIGHT = 10;
	
	private boolean value = false;
	private boolean changed = true;
	
	private Label label;
	private VBox node;
	
	public BooleanProperty(String name) {
		super(name, FlashboardSendableType.BOOLEAN);
		node = new VBox();
		label = new Label(name + ": " + value);
		label.setTextFill(Color.RED);
		label.setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
		node.getChildren().add(label);
	}

	@Override
	protected void update() {
		if(changed){
			label.setText(getName() + ": " + value);
			changed = false;
			label.setTextFill(value? Color.GREEN : Color.RED);
		}
	}
	@Override
	protected Node getNode(){
		return node;
	}
	
	@Override
	public void newData(byte[] bytes) {
		synchronized(this){
			value = bytes[0] == 1;
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
