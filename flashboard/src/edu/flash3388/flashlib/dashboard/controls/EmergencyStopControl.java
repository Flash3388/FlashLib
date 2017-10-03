package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class EmergencyStopControl extends Displayable{

	private static final byte EMERGENCY = 0xe;
	private static final byte NORMAL = 0x5;
	
	private boolean changed = false;
	private boolean emergency = false;
	private boolean update = false;
	
	private Rectangle statusRect;
	private javafx.scene.control.Button button;
	private VBox root;
	
	public EmergencyStopControl() {
		super("Emergency Stop", FlashboardSendableType.ESTOP);
		
		statusRect = new Rectangle();
		statusRect.setWidth(160);
		statusRect.setHeight(40);
		statusRect.setFill(Color.GREEN);
		statusRect.setStroke(Color.BLACK);
		
		button = new javafx.scene.control.Button("Emergency Stop");
		button.setPrefWidth(160);
		button.setPrefHeight(40);
		button.setOnAction((e)->{
			change();
		});
		
		root = new VBox();
		root.setAlignment(Pos.CENTER);
		root.setSpacing(2.0);
		root.getChildren().addAll(button, statusRect);
	}

	public void change(){
		change(!emergency);
	}
	public void change(boolean e){
		emergency = e;
		changed = true;
	}
	
	@Override
	protected void update() {
		if(update){
			update = false;
			
			if(emergency){
				button.setText("Normal");
				statusRect.setFill(Color.RED);
				FlashUtil.getLog().log("!!!EMERGENCY STOP!!!", "EStop Control");
			}else{
				button.setText("Emergency Stop");
				statusRect.setFill(Color.GREEN);
				FlashUtil.getLog().log("Normal Operations", "EStop Control");
			}
		}
	}
	
	public Node getRoot(){
		return root;
	}
	@Override
	protected DisplayType getDisplayType(){
		return DisplayType.Activatable;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 1)return;
		
		if(data[0] == EMERGENCY){
			emergency = true;
		}
		else if(data[0] == NORMAL){
			emergency = false;
		}
		
		update = true;
	} 
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		return new byte[]{emergency? EMERGENCY : NORMAL};
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void onConnection() {
		changed = false;
		emergency = false;
	}
	@Override
	public void onConnectionLost() {
	}
}
