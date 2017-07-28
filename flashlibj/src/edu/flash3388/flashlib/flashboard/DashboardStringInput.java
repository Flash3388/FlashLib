package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.beans.StringProperty;

/**
 * Represents an input for string values on the Flashboard. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardStringInput extends Sendable{
	
	private InputType type;
	private boolean updateType = false;
	private String lastValue, value;
	private StringProperty property;
	
	public DashboardStringInput(String name, StringProperty property, InputType type) {
		super(name, FlashboardSendableType.INPUT);
		this.type = type;
		this.property = property;
	}
	
	public StringProperty valueProperty(){
		return property;
	}
	protected void setProperty(StringProperty property){
		this.property = property;
	}
	
	@Override
	public void newData(byte[] data) {
		String str = new String(data);
		property.set(str);
	}
	@Override
	public byte[] dataForTransmition() {
		if(updateType){
			updateType = false;
			return new byte[] {1, type.value};
		}
		lastValue = value;
		byte[] data = new byte[lastValue.length()+1];
		data[0] = 0;
		System.arraycopy(lastValue.getBytes(), 0, data, 1, lastValue.length());
		return data;
	}
	@Override
	public boolean hasChanged() {
		if(updateType) return true;
		value = property.get();
		return !value.equals(lastValue);
	}
	@Override
	public void onConnection() {
		updateType = true;
		lastValue = null;
	}
	@Override
	public void onConnectionLost() {}
}
