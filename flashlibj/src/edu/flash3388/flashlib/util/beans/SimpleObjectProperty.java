package edu.flash3388.flashlib.util.beans;

public class SimpleObjectProperty<T> implements ObjectProperty<T>{

	private T object;
	
	public SimpleObjectProperty(T o){
		this.object = o;
	}
	public SimpleObjectProperty(){}
	
	@Override
	public T get() {
		return object;
	}
	@Override
	public void set(T o) {
		this.object = o;
	}
}
