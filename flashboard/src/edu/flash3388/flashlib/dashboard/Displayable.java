package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.communications.Sendable;
import javafx.application.Platform;
import javafx.scene.Node;

public abstract class Displayable extends Sendable implements Runnable {

	public static enum DisplayType{
		GraphicData, SimpleData, Input, Activatable
	}
	
	private boolean init = true;
	
	protected Displayable(String name, byte type) {
		super(name, type);
	}

	
	@Override
	public void run() {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(this);
		} else {
			update();
			if(!init()) {
				Node root = setDisplay();
				if(root != null){
					GUI.getMain().addControlToDisplay(root, getDisplayType());
				}
			}
		}
	}
	
	void reset(){
		init = true;
	}
	Node setDisplay(){ 
		init = false;
		return getNode();
	}
	boolean init(){
		return !init;
	}
	
	protected void update(){
	}
	protected Node getNode(){
		return null;
	}
	protected DisplayType getDisplayType(){
		return DisplayType.SimpleData;
	}
}
