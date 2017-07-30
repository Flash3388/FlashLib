package edu.flash3388.flashlib.util.beans;

@FunctionalInterface
public interface ObjectSource<T> {
	
	T get();
}
