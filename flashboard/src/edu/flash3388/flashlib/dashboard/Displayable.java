package edu.flash3388.flashlib.dashboard;

import edu.flash3388.flashlib.communications.Sendable;
import javafx.scene.Node;

public abstract class Displayable extends Sendable{

	public static enum DisplayType{
		GraphicData, SimpleData, Input, Activatable
	}
	
	protected Displayable(String name, byte type) {
		super(name, type);
	}

	private boolean init = true;
	
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
