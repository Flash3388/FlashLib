package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Represents a slider on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardSlider extends Sendable{
	
	private DoubleSource source;
	private double max, min, lastValue;
	private int ticks;
	private boolean changed = false;
	private byte[] data = new byte[20];
	
	public DashboardSlider(String name, double min, double max, int ticks) {
		super(name, FlashboardSendableType.SLIDER);
		this.ticks = ticks;
		this.max = max;
		this.min = min;
		changed = true;
		source = ConstantsHandler.getNumber(getName(), min);
	}

	public int getTicks(){
		return ticks;
	}
	public double maxValue(){
		return max;
	}
	public double minValue(){
		return min;
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
		double value = FlashUtil.toDouble(data);
		ConstantsHandler.putNumber(getName(), value);
		lastValue = value;
	}
	@Override
	public byte[] dataForTransmition() {
		if(changed){
			changed = false;
			FlashUtil.fillByteArray(minValue(), 0, data);
			FlashUtil.fillByteArray(maxValue(), 8, data);
			FlashUtil.fillByteArray(getTicks(), 16, data);
			return data;
		}else{
			lastValue = source.get();
			return FlashUtil.toByteArray(lastValue);
		}
	}
	@Override
	public boolean hasChanged() {
		return changed || source.get() != lastValue;
	}
	@Override
	public void onConnection() {
		changed = true;
		lastValue = source.get() - 1;
	}
	@Override
	public void onConnectionLost() {
	}
}
