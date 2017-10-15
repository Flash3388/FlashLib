package edu.flash3388.flashlib.cams;

import edu.flash3388.flashlib.util.Queue;

/**
 * A camera implementation which uses a {@link Queue} to hold images.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 * @param <T> the type of frame to store
 */
public abstract class QueueCamera<T> implements Camera{
	
	private Queue<T> frames = new Queue<T>();
	
	public void enqueue(T frame){
		frames.enqueue(frame);
	}
	public T dequeue(){
		return frames.dequeue();
	}
	
	@Override
	public Object read() {
		return dequeue();
	}
}
