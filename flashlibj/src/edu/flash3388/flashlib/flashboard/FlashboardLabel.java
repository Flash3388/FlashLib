package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.beans.BooleanSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.StringSource;

public class FlashboardLabel extends Sendable{

	private static interface LabelData{
		boolean changed();
		String get();
	}
	private static class StringSourceLabelData implements LabelData{
		private StringSource source;
		private String value = "";
		private String lastValue = "";
		
		public StringSourceLabelData(StringSource source) {
			this.source = source;
		}
		
		@Override
		public boolean changed() {
			value = source.get();
			return !value.equals(lastValue);
		}
		@Override
		public String get() {
			lastValue = value;
			return value;
		}
	}
	private static class DoubleSourceLabelData implements LabelData{
		private DoubleSource source;
		private double value = 0.0;
		private double lastValue = 0.0;
		
		public DoubleSourceLabelData(DoubleSource source) {
			this.source = source;
		}
		
		@Override
		public boolean changed() {
			value = source.get();
			return Math.abs(value - lastValue) > 0.01;
		}
		@Override
		public String get() {
			lastValue = value;
			return String.valueOf(value);
		}
	}
	private static class BooleanSourceLabelData implements LabelData{
		private BooleanSource source;
		private boolean value = false;
		private boolean lastValue = false;
		
		public BooleanSourceLabelData(BooleanSource source) {
			this.source = source;
		}
		
		@Override
		public boolean changed() {
			value = source.get();
			return value != lastValue;
		}
		@Override
		public String get() {
			lastValue = value;
			return String.valueOf(value);
		}
	}
	
	private boolean changed = false;
	private LabelData data;
	
	public FlashboardLabel(String name, StringSource data) {
		super(name, FlashboardSendableType.LABEL);
		this.data = new StringSourceLabelData(data);
	}
	public FlashboardLabel(String name, DoubleSource data) {
		super(name, FlashboardSendableType.LABEL);
		this.data = new DoubleSourceLabelData(data);
	}
	public FlashboardLabel(String name, BooleanSource data) {
		super(name, FlashboardSendableType.LABEL);
		this.data = new BooleanSourceLabelData(data);
	}
	
	@Override
	public void newData(byte[] data) {}
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		return data.get().getBytes();
	}
	@Override
	public boolean hasChanged() {
		return data.changed() || changed;
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
