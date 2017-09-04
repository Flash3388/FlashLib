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
	private boolean updateType = false, updateValue = false;
	private String lastValue, value;
	private StringProperty property;
	
	public DashboardStringInput(String name, StringProperty property) {
		super(name, FlashboardSendableType.INPUT);
		this.type = InputType.String;
		this.property = property;
	}
	
	protected void setType(InputType type){
		this.type = type;
	}
	protected void setProperty(StringProperty property){
		this.property = property;
		updateValue = true;
	}
	
	public StringProperty valueProperty(){
		return property;
	}
	
	@Override
	public void newData(byte[] data) {
		String str = new String(data);
		property.set(str);
		lastValue = str;
	}
	@Override
	public byte[] dataForTransmition() {
		if(updateType){
			updateType = false;
			return new byte[] {1, type.value};
		}
		updateValue = false;
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
		return updateValue || !value.equals(lastValue);
	}
	@Override
	public void onConnection() {
		updateType = true;
		updateValue = true;
	}
	@Override
	public void onConnectionLost() {}
}
