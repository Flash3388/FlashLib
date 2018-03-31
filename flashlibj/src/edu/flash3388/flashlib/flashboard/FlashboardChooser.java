package edu.flash3388.flashlib.flashboard;

import java.util.Vector;

import edu.flash3388.flashlib.communications.SendableException;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.observable.ObservableIntegerProperty;
import edu.flash3388.flashlib.util.beans.observable.ObservableProperty;
import edu.flash3388.flashlib.util.beans.observable.SimpleObservableIntegerProperty;
import edu.flash3388.flashlib.util.beans.observable.SimpleObservableProperty;

/**
 * Represents a combo box for values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashboardChooser<T> extends FlashboardControl{

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
	private ObservableIntegerProperty selected;
	private ObservableProperty<T> selectedValue;
	private int defaultIndex = -1;
	
	private boolean changed = false, changedIndex = false;
	private int currentChangedIndex = 0;
	
	private Object optionsMutex = new Object();
	private Object selectionMutex = new Object();
	
	@SafeVarargs
	public FlashboardChooser(String name, Option<T>...options) {
		super(name, FlashboardSendableType.CHOOSER);
		
		if(options != null){
			for(Option<T> o : options)
				this.options.addElement(o);
		}
		
		selected = new SimpleObservableIntegerProperty(-1);
		selected.addListener((obs, o, n)->{
			changedIndex = true;
		});
		selectedValue = new SimpleObservableProperty<T>(null);
		selectedValue.addListener((obs, o, n)->{
			int index = -1;
			if(n != null)
				index = indexOf(n);
			
			selected.set(index);
		});
	}
	public FlashboardChooser(String name) {
		this(name, (Option<T>[])null);
	}
	
	public FlashboardChooser<T> addDefault(Option<T> option){
		synchronized (optionsMutex) {
			this.options.addElement(option);
			
			select(options.size() - 1);
			defaultIndex = selected.get();
			
			currentChangedIndex = 0;
			changed = true;	
		}
		
		return this;
	}
	public FlashboardChooser<T> addDefault(String name, T option){
		return this.addDefault(new Option<T>(name, option));
	}
	public FlashboardChooser<T> addOption(Option<T> option){
		synchronized (optionsMutex) {
			this.options.addElement(option);
			
			currentChangedIndex = 0;
			changed = true;	
		}
		
		return this;
	}
	public FlashboardChooser<T> addOption(String name, T option){
		return this.addOption(new Option<T>(name, option));
	}
	public FlashboardChooser<T> remove(int index){
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");

		synchronized (optionsMutex) {
			options.remove(index);
			
			if(options.size() == 0){
				if(index == defaultIndex)
					defaultIndex = -1;
				if(index == selected.get())
					select(-1);
			}
			
			currentChangedIndex = 0;
			changed = true;	
		}
		
		return this;
	}
	public FlashboardChooser<T> removeLast(){
		return remove(options.size()-1);
	}
	
	public Option<T> get(int index){
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		
		Option<T> option = null;
		
		synchronized (optionsMutex) {
			if(index < options.size())
				option = options.get(index);
		}
		
		return option;
	}
	public int indexOf(T object){
		int idx = -1;
		
		synchronized (optionsMutex) {
			for (int i = 0; i < options.size(); i++) {
				Option<T> option = options.get(i);
				if(option.getOption().equals(object)){
					idx = i;
					break;
				}
			}	
		}
		
		return idx;
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
		synchronized (selectionMutex) {
			if(index != selected.get()){
				synchronized (optionsMutex) {
					if(index < 0 || index >= options.size())
						selectedValue.setValue(null);
					else
						selectedValue.setValue(options.get(index).option);		
				}
			}
		}
	}

	@Override
	public void newData(byte[] data) throws SendableException {
		if(data.length < 4) return;
		
		int sel = FlashUtil.toInt(data);
		if(sel >= 0 && sel < options.size() && sel != selected.get()){
			select(sel);
			changedIndex = false;
		}
	}
	@Override
	public byte[] dataForTransmission() throws SendableException {
		if(changedIndex && !changed){
			changedIndex = false;
			byte[] bytes = {1, 0, 0, 0, 0};
			synchronized (selectionMutex) {
				FlashUtil.fillByteArray(selected.get(), 1, bytes);	
			}
			return bytes;
		}
		if(!changedIndex) 
			changedIndex = true;
		
		byte[] bytes;
		
		synchronized (optionsMutex) {
			Option<T> op = options.get(currentChangedIndex);
			
			String name = op.name;
			bytes = new byte[2 + name.length()];
			bytes[0] = 0;
			bytes[1] = (byte) currentChangedIndex;
			System.arraycopy(name.getBytes(), 0, bytes, 2, name.length());
					
			if((++currentChangedIndex) >= options.size())
				changed = false;
		}
		
		return bytes;
	}
	@Override
	public boolean hasChanged() {
		return options.size() > 0 && (changed || changedIndex);
	}
	@Override
	public void onConnection() {
		currentChangedIndex = 0;
		changed = true;
		changedIndex = true;
	}
	@Override
	public void onConnectionLost() {
		changed = false;
		select(defaultIndex);
	}
}
