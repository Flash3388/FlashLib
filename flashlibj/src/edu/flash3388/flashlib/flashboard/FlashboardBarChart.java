package edu.flash3388.flashlib.flashboard;

import java.util.Vector;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class FlashboardBarChart extends FlashboardControl{

	private static class FlashboardBarSeries extends FlashboardControl{

		private DoubleSource source;
		private double value = 0.0, lastValue = 0.0;
		
		private String parentName = null;
		
		private boolean nameUpdated = false;
		private boolean forceDataUpdate = false;
		
		private byte[] databytes = new byte[9];
		
		public FlashboardBarSeries(String name, DoubleSource source, FlashboardBarChart chart) {
			super(name, FlashboardSendableType.BARCHART_SERIES);
			this.source = source;
			setParentName(chart.getName());
		}

		void setParentName(String name){
			if(nameUpdated)
				return;
			this.parentName = name;
		}
		
		@Override
		public void newData(byte[] data) throws SendableException {
		}

		@Override
		public byte[] dataForTransmission() throws SendableException {
			if(!nameUpdated){
				nameUpdated = true;
				byte[] bytes = new byte[parentName.length() + 1];
				bytes[0] = FlashboardBarChart.CHART_NAME_UPDATE;
				System.arraycopy(parentName.getBytes(), 0, bytes, 1, parentName.length());
				return bytes;
			}
			databytes[0] = FlashboardBarChart.DATA_UPDATE;
			FlashUtil.fillByteArray(value, 1, databytes);
			lastValue = value;
			forceDataUpdate = false;
			return databytes;
		}
		@Override
		public boolean hasChanged() {
			if(parentName == null || parentName.isEmpty())
				return false;
			if(!nameUpdated)
				return true;
			value = source.get();
			return forceDataUpdate || Math.abs(value - lastValue) > 0.01;
		}

		@Override
		public void onConnection() {
			forceDataUpdate = true;
			nameUpdated = false;
		}
		@Override
		public void onConnectionLost() {
		}
	}
	
	public static final byte CHART_NAME_UPDATE = 0x1;
	public static final byte DATA_UPDATE = 0x5;
	public static final byte CONFIG_UPDATE = 0x8;
	
	private Vector<FlashboardBarSeries> controls = new Vector<FlashboardBarSeries>();
	private double min = 0.0, max = 0.0;
	private boolean configUpdate = false;
	private byte[] configBytes = new byte[17];
	
	public FlashboardBarChart(String name, double min, double max) {
		super(name, FlashboardSendableType.BARCHART);
		this.max = max;
		this.min = min;
	}

	public FlashboardBarChart addSeries(String name, DoubleSource source){
		controls.add(new FlashboardBarSeries(name, source, this));
		return this;
	}
	public void setValueRange(double min, double max){
		this.min = min;
		this.max = max;
		configUpdate = true;
	}
	
	@Override
	public void newData(byte[] data) {
	}

	@Override
	public byte[] dataForTransmission() {
		configUpdate = false;
		configBytes[0] = CONFIG_UPDATE;
		FlashUtil.fillByteArray(min, 1, configBytes);
		FlashUtil.fillByteArray(max, 9, configBytes);
		return configBytes;
	}
	@Override
	public boolean hasChanged() {
		return configUpdate;
	}

	@Override
	public void onConnection() {
		configUpdate = true;
		
		for (FlashboardBarSeries control : controls) {
			if(!control.isCommunicationAttached())
				Flashboard.attach(control);
		}
	}
	@Override
	public void onConnectionLost() {
	}
}
