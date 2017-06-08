package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.ConstantsHandler;

public class DashboardInput extends Sendable{
	
	private InputType type;
	private boolean updateType = false;
	private String lastValue, value;
	
	public DashboardInput(String name, InputType type) {
		super(name, FlashboardSendableType.INPUT);
		this.type = type;
	}
	
	private String getValueAsString(){
		switch(type){
			case Boolean:return String.valueOf(ConstantsHandler.getBooleanNative(getName()));
			case Double: return String.valueOf(ConstantsHandler.getNumberNative(getName()));
			case String: return ConstantsHandler.getStringNative(getName());
		}
		return null;
	}
	private void setNewValue(String value){
		if(type == InputType.Boolean){
			try {
				boolean b = Boolean.parseBoolean(value);
				ConstantsHandler.putBoolean(getName(), b);
			} catch (NumberFormatException e) { }
		}
		if(type == InputType.Double){
			try {
				double d = Double.parseDouble(value);
				ConstantsHandler.putNumber(getName(), d);
			} catch (NumberFormatException e) { }
		}
		if(type == InputType.String){
			ConstantsHandler.putString(getName(), value);
		}
	}
	
	@Override
	public void newData(byte[] data) {
		String str = new String(data);
		setNewValue(str);
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
		value = getValueAsString();
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
