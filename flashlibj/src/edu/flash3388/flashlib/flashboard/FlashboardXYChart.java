package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class FlashboardXYChart extends Sendable{

	public static enum ChartType{
		Line(FlashboardSendableType.LINECHART), Area(FlashboardSendableType.AREACHART);
		
		public final byte type;
		
		private ChartType(byte type) {
			this.type = type;
		}
	}
	
	public static final byte VALUE_UPDATE = 0x1;
	public static final byte CONFIG_UPDATE = 0x5;
	
	private DoubleSource sourceX, sourceY;
	private double lastX = 0.0, valueX = 0.0;
	private boolean forceUpdate = false;
	
	private double minY = 0.0, maxY = 1.0, minX = 0.0, maxX = 1.0;
	private boolean updateConfig = false;
	
	private byte[] send = new byte[17];
	
	public FlashboardXYChart(String name, ChartType type, DoubleSource xSource, DoubleSource ySource, 
			double minX, double maxX, double minY, double maxY) {
		super(name, type.type);
		
		this.sourceX = xSource;
		this.sourceY = ySource;
		
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public void setMinY(double minY){
		this.minY = minY;
		updateConfig = true;
	}
	public void setMaxY(double maxY){
		this.maxY = maxY;
		updateConfig = true;
	}
	public void setMinX(double minX){
		this.minX = minX;
		updateConfig = true;
	}
	public void setMaxX(double maxX){
		this.maxX = maxX;
		updateConfig = true;
	}
	
	@Override
	public void newData(byte[] data) {
	}

	@Override
	public byte[] dataForTransmition() {
		if(updateConfig){
			updateConfig = false;
			byte[] bytes = new byte[33];
			bytes[0] = CONFIG_UPDATE;
			FlashUtil.fillByteArray(minX, 1, bytes);
			FlashUtil.fillByteArray(maxX, 9, bytes);
			FlashUtil.fillByteArray(minY, 17, bytes);
			FlashUtil.fillByteArray(maxY, 25, bytes);
			return bytes;
		}
		
		send[0] = VALUE_UPDATE;
		lastX = valueX;
		forceUpdate = false;
		FlashUtil.fillByteArray(valueX, 1, send);
		FlashUtil.fillByteArray(sourceY.get(), 9, send);
		return send;
	}
	@Override
	public boolean hasChanged() {
		valueX = sourceX.get();
		return updateConfig || forceUpdate || Math.abs(valueX - lastX) > 0.001;
	}

	@Override
	public void onConnection() {
		forceUpdate = true;
		updateConfig = true;
	}
	@Override
	public void onConnectionLost() {
	}
}
