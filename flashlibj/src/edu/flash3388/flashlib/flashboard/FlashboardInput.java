package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.StringProperty;

public class FlashboardInput extends FlashboardControl{

	private static interface InputData{
		boolean changed();
		String get();
		void set(String val);
	}
	private static class StringInputData implements InputData{
		private StringProperty prop;
		private String value = "";
		private String lastValue = "";
		
		public StringInputData(StringProperty prop) {
			this.prop = prop;
		}
		
		@Override
		public boolean changed() {
			value = prop.get();
			return !value.equals(lastValue);
		}
		@Override
		public String get() {
			lastValue = value;
			return value;
		}
		@Override
		public void set(String val) {
			prop.set(val);
			lastValue = val;
		}
	}
	private static class DoubleInputData implements InputData{
		private DoubleProperty prop;
		private double value = 0.0;
		private double lastValue = 0.0;
		private boolean forceUpdate = false;
		
		public DoubleInputData(DoubleProperty prop) {
			this.prop = prop;
		}
		
		@Override
		public boolean changed() {
			value = prop.get();
			return forceUpdate || Math.abs(value - lastValue) > 0.01;
		}
		@Override
		public String get() {
			lastValue = value;
			forceUpdate = false;
			return String.valueOf(Mathf.roundDecimal(value, 3));
		}
		@Override
		public void set(String val) {
			try{
				double d = Double.parseDouble(val);
				prop.set(d);
				lastValue = d;
			}catch(NumberFormatException e){
				forceUpdate = true;
			}
		}
	}
	private static class BooleanInputData implements InputData{
		private BooleanProperty prop;
		private boolean value = false;
		private boolean lastValue = false;
		private boolean forceUpdate = false;
		
		public BooleanInputData(BooleanProperty prop) {
			this.prop = prop;
		}
		
		@Override
		public boolean changed() {
			value = prop.get();
			return forceUpdate || value != lastValue;
		}
		@Override
		public String get() {
			lastValue = value;
			forceUpdate = false;
			return String.valueOf(value);
		}
		@Override
		public void set(String val) {
			try{
				boolean b = Boolean.parseBoolean(val);
				prop.set(b);
				lastValue = b;
			}catch(NumberFormatException e){
				forceUpdate = true;
			}
		}
	}
	
	private boolean changed = false;
	private InputData data;
	
	public FlashboardInput(String name, StringProperty data) {
		super(name, FlashboardSendableType.INPUT);
		this.data = new StringInputData(data);
	}
	public FlashboardInput(String name, DoubleProperty data) {
		super(name, FlashboardSendableType.INPUT);
		this.data = new DoubleInputData(data);
	}
	public FlashboardInput(String name, BooleanProperty data) {
		super(name, FlashboardSendableType.INPUT);
		this.data = new BooleanInputData(data);
	}
	
	@Override
	public void newData(byte[] data) throws SendableException {
		String str = new String(data);
		this.data.set(str);	
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		changed = false;
		byte[] data = this.data.get().getBytes();
		
		return data;
	}
	@Override
	public boolean hasChanged() {
		return changed || this.data.changed();
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
