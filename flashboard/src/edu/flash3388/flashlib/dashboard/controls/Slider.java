package edu.flash3388.flashlib.dashboard.controls;

import edu.flash3388.dashboard.Displayble;
import edu.flash3388.flashlib.flashboard.FlashboardSendableType;
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
	private double min, max;
	private int ticks;
	private Runnable updater;
	private boolean changed = false;
	private byte[] data = new byte[8];
	
	public Slider(String name, int id) {
		super(name, id, FlashboardSendableType.SLIDER);
		label = new Label(name);
		slider = new javafx.scene.control.Slider();
		value = new SimpleDoubleProperty();
		slider.valueProperty().addListener((observable, oldValue, newValue)->{
			value.set(newValue.doubleValue());
			FlashUtil.fillByteArray(value.get(), data);
			changed = true;
		});
		container = new VBox();
		container.setSpacing(5);
		container.getChildren().addAll(label, slider);
		
		min = 0;
		max = 1;
		ticks = 10;
		
		updater = ()->{
			if(min != slider.getMin())
				slider.setMin(min);
			if(max != slider.getMax())
				slider.setMax(max);
			if(ticks != slider.getMinorTickCount())
				slider.setMinorTickCount(ticks);
		};
	}

	@Override
	public Runnable updateDisplay(){
		return updater;
	}
	@Override
	protected Node getNode(){
		return container;
	}
	
	@Override
	public void newData(byte[] data) {
		if(data.length < 20) return;
		min = FlashUtil.toDouble(data, 0);
		max = FlashUtil.toDouble(data, 8);
		ticks = FlashUtil.toInt(data, 16);
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
