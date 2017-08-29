package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.communications.Sendable;
import javafx.scene.Node;

public abstract class Displayble extends Sendable{

	public static enum DisplayType{
		Cam, Data, Controller, Manual
	}
	
	protected Displayble(String name, byte type) {
		super(name, type);
	}

	private boolean init = true;
	
	public void reset(){
		init = true;
	}
	public Node setDisplay(){ 
		init = false;
		return getNode();
	}
	public boolean init(){return !init;}
	public Runnable updateDisplay(){return null;}
	public void update(){}
	protected Node getNode(){return null;}
	public DisplayType getDisplayType(){
		return DisplayType.Data;
	}
}
