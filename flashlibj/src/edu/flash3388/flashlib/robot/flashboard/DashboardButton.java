package edu.flash3388.flashlib.robot.flashboard;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.util.FlashUtil;

public class DashboardButton extends Sendable{
	
	private Vector<Action> actions = new Vector<Action>(2);
	private boolean activated = false;
	private byte[] done = {1};
	
	public DashboardButton(String name) {
		super(name, FlashboardSendableType.ACTIVATABLE);
	}

	public void whenPressed(Action action){
		actions.add(action);
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == 1){
			FlashUtil.getLog().log("Start");
			for(Enumeration<Action> eA = actions.elements(); eA.hasMoreElements();){
				Action a = eA.nextElement();
				if(!a.isRunning())
					a.start();
			}
			activated = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		for(Enumeration<Action> eA = actions.elements(); eA.hasMoreElements();){
			if(eA.nextElement().isRunning())
				return null;
		}
		activated = false;
		return done;
	}
	@Override
	public boolean hasChanged() {
		return activated;
	}
	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
	}
}
