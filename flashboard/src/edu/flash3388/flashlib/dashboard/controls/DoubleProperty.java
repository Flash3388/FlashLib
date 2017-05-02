package edu.flash3388.flashlib.dashboard.controls;

import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DoubleProperty extends Displayble{

	private static final int LABEL_WIDTH = 150;
	private static final int LABEL_HEIGHT = 10;
	
	private double value = 0.0;
	
	private Label label;
	private VBox node;
	private Runnable updater;
	
	public DoubleProperty(String name, int id) {
		super(name, id, FlashboardSendableType.DOUBLE);
		node = new VBox();
		label = new Label(name + ": " + value);
		label.setPrefSize(LABEL_WIDTH, LABEL_HEIGHT);
		node.getChildren().add(label);
		
		updater = new Runnable(){
			@Override
			public void run() {
				label.setText(name + ": " + value);
			}
		};
	}

	@Override
	public void newData(byte[] bytes) {
		if(bytes.length < 8) return;
		if(bytes.length > 8) bytes = Arrays.copyOfRange(bytes, 0, 8);
		synchronized(this){
			value = ByteBuffer.wrap(bytes).getDouble();
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
	@Override
	protected Node getNode(){return node;}
	@Override
	public Runnable updateDisplay() {
		return updater;
	}
}
