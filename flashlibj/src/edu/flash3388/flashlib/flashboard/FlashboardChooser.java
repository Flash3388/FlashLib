package edu.flash3388.flashlib.flashboard;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleProperty;

/**
 * Represents a combo box for values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardChooser<T> extends Sendable{

	public static class Option<T>{
		private String name;
		private T option;
		
		public Option(String name, T option){
			this.name = name;
			this.option = option;
		}
		
		public String getName(){
			return name;
		}
		public T getOption(){
			return option;
		}
	}
	
	private Vector<Option<T>> options = new Vector<Option<T>>();
	private IntegerProperty selected = new SimpleIntegerProperty();
	private Property<T> selectedValue = new SimpleProperty<T>(null);
	private int defaultIndex = -1;
	private boolean changed = false, changedIndex = false;
	
	@SafeVarargs
	public FlashboardChooser(String name, Option<T>...options) {
		super(name, FlashboardSendableType.CHOOSER);
		
		if(options != null){
			for(Option<T> o : options)
				this.options.addElement(o);
		}
	}
	public FlashboardChooser(String name) {
		this(name, (Option<T>[])null);
	}
	
	public FlashboardChooser<T> addDefault(Option<T> option){
		this.options.addElement(option);
		select(options.size() - 1);
		defaultIndex = selected.get();
		changed = true;
		return this;
	}
	public FlashboardChooser<T> addDefault(String name, T option){
		return this.addDefault(new Option<T>(name, option));
	}
	public FlashboardChooser<T> addOption(Option<T> option){
		this.options.addElement(option);
		changed = true;
		return this;
	}
	public FlashboardChooser<T> addOption(String name, T option){
		return this.addOption(new Option<T>(name, option));
	}
	public FlashboardChooser<T> remove(int index){
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");

		options.remove(index);
		
		if(options.size() == 0){
			if(index == defaultIndex)
				defaultIndex = -1;
			if(index == selected.get())
				select(-1);
		}
		
		changed = true;
		return this;
	}
	public FlashboardChooser<T> removeLast(){
		return remove(options.size()-1);
	}
	
	public Option<T> get(int index){
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		
		return options.get(index);
	}
	public int indexOf(T object){
		for (int i = 0; i < options.size(); i++) {
			Option<T> option = options.get(i);
			if(option.getOption().equals(object))
				return i;
		}
		return -1;
	}
	
	public IntegerProperty selectedIndexProperty(){
		return selected;
	}
	public Property<T> selectedValueProperty(){
		return selectedValue;
	}
	
	public T getSelected(){
		return selectedValue.getValue();
	}
	public int getSelectedIndex(){
		return selected.get();
	}
	public void select(int index){
		setSelected(index);
		changedIndex = true;
	}
	
	private void setSelected(int index){
		selected.set(index);
		if(index < 0)
			selectedValue.setValue(null);
		else
			selectedValue.setValue(options.get(selected.get()).option);
	}

	@Override
	public void newData(byte[] data) {
		if(data.length < 4) return;
		int sel = FlashUtil.toInt(data);
		if(sel >= 0 && sel < options.size() && sel != selected.get())
			setSelected(sel);
	}
	@Override
	public byte[] dataForTransmition() {
		if(changedIndex && !changed){
			changedIndex = false;
			byte[] bytes = {1, 0, 0, 0, 0};
			FlashUtil.fillByteArray(selected.get(), 1, bytes);
			return bytes;
		}
		changed = false;
		if(!changedIndex) changedIndex = true;
		String all = "";
		for(Enumeration<Option<T>> opEnum = options.elements(); opEnum.hasMoreElements();)
			all += opEnum.nextElement().name + ":";
		all = all.substring(0, all.length() - 1);
		byte[] bytes = new byte[all.length() + 1];
		bytes[0] = 2;
		System.arraycopy(all.getBytes(), 0, bytes, 1, all.length());
		return bytes;
	}
	@Override
	public boolean hasChanged() {
		return options.size() > 0 && (changed || changedIndex);
	}
	@Override
	public void onConnection() {
		changed = true;
	}
	@Override
	public void onConnectionLost() {
		changed = false;
		setSelected(defaultIndex);
	}
}
