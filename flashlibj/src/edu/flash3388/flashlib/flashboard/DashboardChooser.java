package edu.flash3388.flashlib.flashboard;

import java.util.Enumeration;
import java.util.Vector;

import edu.flash3388.flashlib.communications.Sendable;
import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents a combo box for values on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DashboardChooser<T> extends Sendable{

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
	private int selected = 0, defaultIndex = -1;
	private boolean changed = false, changedIndex = false;
	
	@SafeVarargs
	public DashboardChooser(String name, Option<T>...options) {
		super(name, FlashboardSendableType.CHOOSER);
		
		if(options != null){
			for(Option<T> o : options)
				this.options.addElement(o);
		}
	}
	public DashboardChooser(String name) {
		this(name, (Option<T>[])null);
	}
	
	public DashboardChooser<T> addDefault(Option<T> option){
		select(options.size());
		defaultIndex = selected;
		this.options.addElement(option);
		changed = true;
		return this;
	}
	public DashboardChooser<T> addDefault(String name, T option){
		return this.addOption(new Option<T>(name, option));
	}
	public DashboardChooser<T> addOption(Option<T> option){
		this.options.addElement(option);
		changed = true;
		return this;
	}
	public DashboardChooser<T> addOption(String name, T option){
		return this.addOption(new Option<T>(name, option));
	}
	public DashboardChooser<T> remove(int index){
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		options.remove(index);
		changed = true;
		return this;
	}
	public DashboardChooser<T> removeLast(){
		return remove(options.size()-1);
	}
	public T getSelected(){
		return selected >= 0 && selected < options.size() && options.size() > 0 ? options.get(selected).option : null;
	}
	public int getSelectedIndex(){
		return selected >= 0 && selected < options.size() && options.size() > 0 ? selected : -1;
	}
	public void select(int index){
		selected = index;
		changedIndex = true;
	}

	@Override
	public void newData(byte[] data) {
		if(data.length < 4) return;
		int sel = FlashUtil.toInt(data);
		if(sel >= 0 && sel < options.size() && sel != selected)
			selected = sel;
	}
	@Override
	public byte[] dataForTransmition() {
		if(changedIndex && !changed){
			changedIndex = false;
			byte[] bytes = {1, 0, 0, 0, 0};
			FlashUtil.fillByteArray(selected, 1, bytes);
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
		selected = defaultIndex >= 0 ? defaultIndex : 0;
	}
	@Override
	public void onConnectionLost() {
		changed = false;
	}
}
