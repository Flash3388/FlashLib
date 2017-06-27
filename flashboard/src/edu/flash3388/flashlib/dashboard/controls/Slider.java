package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.flashlib.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
import edu.flash3388.flashlib.math.Mathf;
import edu.flash3388.flashlib.util.FlashUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Slider extends Displayble{
	
	private javafx.scene.control.Slider slider;
	private Label label;
	private VBox container;
	private SimpleDoubleProperty value;
	private double min, max, newValue = 0;
	private int ticks;
	private Runnable updater;
	private boolean changed = false, local = false, valChanged = false, update = true;
	private byte[] data = new byte[8];
	
	public Slider(String name, int id) {
		super(name, id, FlashboardSendableType.SLIDER);
		label = new Label(name);
		slider = new javafx.scene.control.Slider();
		value = new SimpleDoubleProperty();
		slider.valueProperty().addListener((observable, oldValue, newValue)->{
			if(local) return;
			value.set(newValue.doubleValue());
			this.newValue = value.get();
			label.setText(getName()+": "+Mathf.roundDecimal(this.newValue));
			FlashUtil.fillByteArray(value.get(), data);
			changed = true;
		});
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setSnapToTicks(true);
		container = new VBox();
		container.setSpacing(5);
		container.getChildren().addAll(label, slider);
		
		min = 0;
		max = 1;
		ticks = 10;
		
		updater = ()->{
			update = false;
			if(min != slider.getMin()){
				slider.setMin(min);
				slider.setBlockIncrement((max - min) / ticks);
				slider.setMajorTickUnit(max - min);
			}
			if(max != slider.getMax()){
				slider.setMax(max);
				slider.setBlockIncrement((max - min) / ticks);
				slider.setMajorTickUnit(max - min);
			}
			if(ticks != slider.getMinorTickCount()){
				slider.setMinorTickCount(ticks);
				slider.setBlockIncrement((max - min) / ticks);
				slider.setMajorTickUnit(max - min);
			}
			if(valChanged){
				valChanged = false;
				local = true;
				value.set(newValue);
				slider.setValue(newValue);
				label.setText(getName()+": "+Mathf.roundDecimal(this.newValue));
				local = false;
			}
		};
	}

	@Override
	public Runnable updateDisplay(){
		if(!update) return null;
		return updater;
	}
	@Override
	protected Node getNode(){
		return container;
	}
	@Override
	public DisplayType getDisplayType(){
		return DisplayType.Manual;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length == 8){
			newValue = FlashUtil.toDouble(data);
			valChanged = true;
			update = true;
		}
		else if(data.length == 20){
			min = FlashUtil.toDouble(data, 0);
			max = FlashUtil.toDouble(data, 8);
			ticks = FlashUtil.toInt(data, 16);
			update = true;
		}
	}
	@Override
	public byte[] dataForTransmition() {
		changed = false;
		return data;
	}
	@Override
	public boolean hasChanged() {
		return changed;
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {}
}
