package edu.flash3388.flashlib.dashboard;

import java.util.concurrent.atomic.AtomicBoolean;

import edu.flash3388.flashlib.communications.Sendable;
import javafx.application.Platform;
import javafx.scene.Node;

public abstract class Displayable extends Sendable implements Runnable {

	public static enum DisplayType{
		GraphicData, SimpleData, Input, Activatable
	}
	
	private AtomicBoolean needInit = new AtomicBoolean(true);
	private AtomicBoolean shouldUpdate = new AtomicBoolean(true);
	
	protected Displayable(String name, byte type) {
		super(name, type);
	}

	@Override
	public void run() {
		if (!Platform.isFxApplicationThread()) {
			if (shouldUpdate.get()) {
				Platform.runLater(this);
				shouldUpdate.set(false);
			}
		} else {
			update();
			if(!needInit.get()) {
				needInit.set(false);
				Node root = getNode();
				if(root != null){
					GUI.getMain().addControlToDisplay(root, getDisplayType());
				}
			}
			shouldUpdate.set(true);
		}
	}
	
	void reset(){
		needInit.set(true);
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
