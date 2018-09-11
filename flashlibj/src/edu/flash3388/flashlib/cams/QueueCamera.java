package edu.flash3388.flashlib.cams;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * A camera implementation which uses a {@link Queue} to hold images.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @param <T> the type of frame to store
 */
public abstract class QueueCamera<T> implements Camera{
	
	private Queue<T> frames = new ArrayDeque<>();
	
	public void enqueue(T frame){
		frames.add(frame);
	}
	public T dequeue(){
		return frames.remove();
	}
	
	@Override
	public Object read() {
		return dequeue();
	}
}
