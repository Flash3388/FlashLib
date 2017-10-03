package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardButton;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.gui.FlashFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ButtonControl extends Displayable{

	private static final int WIDTH = 100;
	private static final int HEIGHT = 30;
	
	private Button button;
	private HBox node;
	private byte[] press = {FlashboardButton.DOWN};
	private boolean changed = false;
	
	public ButtonControl(String name) {
		super(name, FlashboardSendableType.ACTIVATABLE);
		node = new HBox();
		node.setAlignment(Pos.CENTER);
		
		button = new Button(name);
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
		press[0] = FlashboardButton.DOWN;
		changed = true;
	}
	
	@Override
	protected Node getNode(){
		return node;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Activatable;
	}
	
	@Override
	public void newData(byte[] bytes) {
		if(bytes[0] == FlashboardButton.UP){
			FlashFXUtils.onFXThread(()->{
				button.setDisable(false);
			});
			press[0] = FlashboardButton.UP;
			changed = true;
		}else if(bytes[0] == FlashboardButton.ENABLED){
			final boolean enabled = bytes[1] == 1;
			FlashFXUtils.onFXThread(()->{
				button.setDisable(enabled);
			});
			press[0] = FlashboardButton.UP;
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
}
