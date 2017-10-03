package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DoubleProperty extends Displayable{

	private static final int LABEL_WIDTH = 150;
	private static final int LABEL_HEIGHT = 10;
	
	private double value = 0.0;
	
	private Label label;
	private VBox node;
	private boolean changed = true;
	
	public DoubleProperty(String name) {
		super(name, FlashboardSendableType.DOUBLE);
		node = new VBox();
		label = new Label(name + ": " + value);
		label.setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
		node.getChildren().add(label);
	}

	@Override
	protected Node getNode(){
		return node;
	}
	@Override
	protected void update() {
		if(changed){
			changed = false;
			label.setText(getName() + ": " + value);
		}
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(bytes.length < 8) return;
		synchronized(this){
			value = FlashUtil.toDouble(bytes);
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
