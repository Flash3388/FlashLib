package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

public class DashboardSlider extends Sendable{
	
	private double max, min, value;
	private int ticks;
	private boolean changed = false;
	private byte[] data = new byte[20];
	
	public DashboardSlider(String name, double min, double max, int ticks) {
		super(name, FlashboardSendableType.SLIDER);
		this.ticks = ticks;
		this.max = max;
		this.min = min;
		changed = true;
	}

	public double getTicks(){
		return ticks;
	}
	public double maxValue(){
		return max;
	}
	public double minValue(){
		return min;
	}
	public double doubleValue(){
		return value;
	}
	public void setMinValue(double min){
		this.min = min;
		changed = true;
	}
	public void setMaxValue(double max){
		this.max = max;
		changed = true;
	}
	public void setTicks(int ticks){
		this.ticks = ticks;
		changed = true;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 8) return;
		value = FlashUtil.toDouble(data);
	}
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		FlashUtil.fillByteArray(minValue(), 0, data);
		FlashUtil.fillByteArray(maxValue(), 8, data);
		FlashUtil.fillByteArray(getTicks(), 16, data);
		return data;
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
