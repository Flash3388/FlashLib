package edu.flash3388.flashlib.util.beans;

@FunctionalInterface
public interface ValueSource<T> {
	
	T getValue();
}
