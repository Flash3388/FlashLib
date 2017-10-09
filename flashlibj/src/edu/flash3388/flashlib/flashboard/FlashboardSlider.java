package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

/**
 * Represents a slider on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardSlider extends FlashboardControl{
	
	private DoubleProperty valData;
	private double max, min, lastValue;
	private int ticks;
	private boolean changed = false, updateValue = false;
	private byte[] data = new byte[20];
	
	public FlashboardSlider(String name, DoubleProperty data, double min, double max, int ticks) {
		super(name, FlashboardSendableType.SLIDER);
		this.ticks = ticks;
		this.max = max;
		this.min = min;
		this.valData = data;
		changed = true;
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
	
	public DoubleProperty valueProperty(){
		return valData;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 8) return;
		double value = FlashUtil.toDouble(data);
		valData.set(value);
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
		}
		updateValue = false;
		lastValue = valData.get();
		return FlashUtil.toByteArray(lastValue);
	}
	@Override
	public boolean hasChanged() {
		return changed || updateValue || valData.get() != lastValue;
	}
	@Override
	public void onConnection() {
		changed = true;
		updateValue = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
