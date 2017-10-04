package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BooleanIndicatorControl extends Displayable{

	public static final double HEIGHT = 30.0;
	public static final double WIDTH = 60.0;
	
	private Rectangle indicator;
	private VBox root;
	
	private boolean value = false, update = false;
	private Object valueMutex = new Object();
	
	public BooleanIndicatorControl(String name) {
		super(name, FlashboardSendableType.BOOL_INDICATOR);
		
		indicator = new Rectangle(WIDTH, HEIGHT, Color.RED);
		root = new VBox();
		root.setSpacing(5.0);
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(indicator, new Label(name));
	}

	@Override
	protected Node getNode() {
		return root;
	}
	@Override
	protected DisplayType getDisplayType() {
		return DisplayType.GraphicData;
	}
	@Override
	protected void update() {
		synchronized (valueMutex) {
			if(update){
				indicator.setFill(value? Color.DARKGREEN : Color.RED);
				update = false;
			}
		}
	}
	
	@Override
	public void newData(byte[] data) {
		synchronized (valueMutex) {
			value = data[0] == 1;
			update = true;
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
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
}
