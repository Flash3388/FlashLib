package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.dashboard.Displayable;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.gui.CircularDirectionIndicator;
import edu.flash3388.flashlib.util.FlashUtil;

import javafx.scene.Node;

public class DirectionControl extends Displayable{

	private static final double RADIUS = 40;
	
	private CircularDirectionIndicator indicator;
	private boolean update = true;
	private double value = 0.0;
	private Object valueMutex = new Object();
	
	public DirectionControl(String name) {
		super(name, FlashboardSendableType.DIR_INDICATOR);
		
		indicator = new CircularDirectionIndicator(name, RADIUS);
	}

	@Override
	protected Node getNode() {
		return indicator.getRoot();
	}
	@Override
	protected DisplayType getDisplayType() {
		return DisplayType.GraphicData;
	}
	@Override
	protected void update() {
		synchronized (valueMutex) {
			if(update){
				indicator.setValue(value);
				update = false;
			}
		}
	}
	
	@Override
	public void newData(byte[] data) throws SendableException {
		synchronized (valueMutex) {
			value = FlashUtil.toDouble(data);
			update = true;
		}
	}

	@Override
	public byte[] dataForTransmission() throws SendableException {
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
