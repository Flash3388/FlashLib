package edu.flash3388.flashlib.dashboard.controls;

import java.util.HashMap;

import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.flashboard.PidTuner;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.StackPane;

public class DashboardPidTuner extends Displayble{

	private static final HashMap<String, DashboardPidTuner> tuners = new HashMap<String, DashboardPidTuner>();
	
	private LineChart.Series<Number, Number> series;
	private Data<Number, Number> lastdata;
	
	private SimpleDoubleProperty kp, ki, kd, setpoint, valueBinder, timeBinder, funcPeriodBinder;
	private SimpleStringProperty setpointBinder;
	private double lastkp, lastki, lastkd, lastsetpoint;
	private double newValue, lastValue;
	private long millisMaxFuncValue = -1;
	private double maxValue = 10.0;
	private int ticks = 2000;
	
	private long startTime = 0;
	private double lastTimeUpdate = -1;
	
	private boolean update = false, remoteUpdate = false, setUpdate = false,
			newValueSet = false, newSetpointSet = false, newKSet = false, lastValBigger = false;
	
	public DashboardPidTuner(String name, int id) {
		super(name, id, FlashboardSendableType.PIDTUNER);
		
		series = new LineChart.Series<Number, Number>();
		
		kp = new SimpleDoubleProperty();
		ki = new SimpleDoubleProperty();
		kd = new SimpleDoubleProperty();
		setpoint = new SimpleDoubleProperty();
		valueBinder = new SimpleDoubleProperty();
		timeBinder = new SimpleDoubleProperty();
		funcPeriodBinder = new SimpleDoubleProperty();
		
		setpointBinder = new SimpleStringProperty("0.0");
		
		tuners.put(name, this);
	}

	public LineChart.Series<Number, Number> getSeries(){
		return series;
	}
	
	public SimpleDoubleProperty periodBindProperty(){
		return funcPeriodBinder;
	}
	public SimpleDoubleProperty valueBindProperty(){
		return valueBinder;
	}
	public SimpleDoubleProperty timeBindProperty(){
		return timeBinder;
	}
	public SimpleStringProperty setPointBindProperty(){
		return setpointBinder;
	}
	public SimpleDoubleProperty setpointProperty(){
		return setpoint;
	}
	public SimpleDoubleProperty kpProperty(){
		return kp;
	}
	public SimpleDoubleProperty kiProperty(){
		return ki;
	}
	public SimpleDoubleProperty kdProperty(){
		return kd;
	}
	
	public double getSliderMaxValue(){
		return maxValue;
	}
	public int getSliderTicks(){
		return ticks;
	}
	
	public void setUpdate(boolean update){
		if(this.update == update)
			return;
		
		if(update){
			startTime = FlashUtil.millis();
		}
		
		millisMaxFuncValue = -1;
		lastValBigger = false;
		lastValue = 0;
		lastTimeUpdate = -1;
		lastdata = null;
		series.getData().clear();
		
		this.update = update;
		setUpdate = true;
	}
	
	@Override
	public void update() {
		if(!update || !remoteUpdate) return;
		
		double time = (FlashUtil.millis() - startTime) * 0.001;
		
		if(lastTimeUpdate < 0 || time - lastTimeUpdate > 0.1){
			timeBinder.set(Mathf.roundDecimal(time));
			lastTimeUpdate = time;
		}
		
		if (newValueSet) {
			newValueSet = false;
			Data<Number, Number> data = new Data<Number, Number>(time, newValue);
			series.getData().add(data);
			((StackPane)data.getNode()).setVisible(false);
			valueBinder.set(Mathf.roundDecimal(newValue, 4));
			
			if(!lastValBigger && lastValue < newValue)
				lastValBigger = true;
			else if(lastValBigger && newValue < lastValue){
				lastValBigger = false;
				if(millisMaxFuncValue < 0)
					millisMaxFuncValue = FlashUtil.millis();
				else{
					double t = (FlashUtil.millis() - millisMaxFuncValue) * 0.001;
					funcPeriodBinder.set(t);
					if(lastdata != null)
						((StackPane)lastdata.getNode()).setVisible(true);
					millisMaxFuncValue = FlashUtil.millis();
				}
			}
			
			lastValue = newValue;
			lastdata = data;
		}
		if(newSetpointSet){
			setpoint.set(lastsetpoint);
			setpointBinder.set(String.valueOf(Mathf.roundDecimal(setpoint.get(), 4)));
			newSetpointSet = false;
		}
		if(newKSet){
			kp.set(lastkp);
			ki.set(lastki);
			kd.set(lastkd);
			newKSet = false;
		}
	}
	
	@Override
	public void newData(byte[] data) {
		if(data[0] == PidTuner.K_UPDATE){
			lastkp = FlashUtil.toDouble(data, 1);
			lastki = FlashUtil.toDouble(data, 9);
			lastkd = FlashUtil.toDouble(data, 17);
			newKSet = true;
		}
		else if(data[0] == PidTuner.CV_UPDATE){
			newValue = FlashUtil.toDouble(data, 1);
			newValueSet = true;
		}
		else if(data[0] == PidTuner.SP_UPDATE){
			lastsetpoint = FlashUtil.toDouble(data, 1);
			newSetpointSet = true;
		}
		else if(data[0] == PidTuner.SLIDER_UPDATE){
			System.out.println("Slider update");
			maxValue = FlashUtil.toDouble(data, 1);
			ticks = FlashUtil.toInt(data, 9);
		}
		remoteUpdate = true;
	}
	@Override
	public byte[] dataForTransmition() {
		if(setUpdate){
			setUpdate = false;
			return new byte[]{PidTuner.RUN_UPDATE, (byte) (update? 1 : 0)};
		}
		if(!newSetpointSet && lastsetpoint != setpoint.get()){
			lastsetpoint = setpoint.get();
			
			byte[] data = new byte[9];
			data[0] = PidTuner.SP_UPDATE;
			FlashUtil.fillByteArray(lastsetpoint, 1, data);
			return data;
		}
		if(!newKSet && (lastkp != kp.get() || lastki != ki.get() || lastkd != kd.get())){
			lastkp = kp.get();
			lastki = ki.get();
			lastkd = kd.get();
			
			byte[] data = new byte[25];
			data[0] = PidTuner.K_UPDATE;
			FlashUtil.fillByteArray(lastkp, 1, data);
			FlashUtil.fillByteArray(lastki, 9, data);
			FlashUtil.fillByteArray(lastkd, 17, data);
			
			return data;
		}

		return null;
	}
	@Override
	public boolean hasChanged() {
		return update || setUpdate;
	}

	@Override
	public void onConnection() {
	}
	@Override
	public void onConnectionLost() {
		
	}
	
	
	public static DashboardPidTuner getTuner(String name){
		return tuners.get(name);
	}
	public static String[] getTunerNames(){
		return tuners.keySet().toArray(new String[tuners.keySet().size()]);
	}
	public static void resetTuners(){
		String[] tunersN = getTunerNames();
		for (int i = 0; i < tunersN.length; i++){
		}
		tuners.clear();
	}
}
