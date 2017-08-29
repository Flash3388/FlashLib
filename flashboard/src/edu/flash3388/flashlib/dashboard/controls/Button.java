package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.DashboardButton;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.gui.FlashFxUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Button extends Displayble{

	private static final int WIDTH = 100;
	private static final int HEIGHT = 30;
	
	private javafx.scene.control.Button button;
	private HBox node;
	private byte[] press = {DashboardButton.DOWN};
	private boolean changed = false;
	
	public Button(String name) {
		super(name, FlashboardSendableType.ACTIVATABLE);
		node = new HBox();
		node.setAlignment(Pos.TOP_CENTER);
		
		button = new javafx.scene.control.Button(name);
		button.setPrefSize(WIDTH, HEIGHT);
		button.setOnAction(new EventHandler<ActionEvent>(){
			@Override
			public void handle(ActionEvent event) {
				press();
				button.setDisable(true);
			}
		});
		node.getChildren().add(button);
		button.setAlignment(Pos.CENTER);
	}

	private void press(){
		press[0] = DashboardButton.DOWN;
		changed = true;
	}
	@Override
	public void newData(byte[] bytes) {
		if(bytes[0] == DashboardButton.UP){
			FlashFxUtils.onFxThread(()->{
				button.setDisable(false);
			});
			press[0] = DashboardButton.UP;
			changed = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		return press;
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}
	@Override
	public void onConnection() {}
	@Override
	public void onConnectionLost() {}
	
	@Override
	protected Node getNode(){return node;}
	@Override
	public DisplayType getDisplayType(){
		return DisplayType.Manual;
	}
}
