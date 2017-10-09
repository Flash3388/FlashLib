package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;

public abstract class FlashboardControl extends Sendable{

	protected FlashboardControl(String name, byte type) {
		super(name, type);
	}
	protected FlashboardControl(byte type) {
		super(type);
	}

	public void attachToFlashboard(){
		if(!attached()){
			Flashboard.attach(this);
		}
	}
}
