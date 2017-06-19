package edu.flash3388.flashlib.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates an object which tracks instances of other objects as {@link java.lang.ref.WeakReference}s.
 * For this to work, it requires creation of instances through this class using the {@link #createInstance()} method.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class InstanceTracker {
	
	private List<WeakReference<?>> instances; 
	private Class<?> cl;
	
	/**
	 * Creates a tracker for instances of a given {@link Class} type.
	 * 
	 * @param cl the {@link Class} to track.
	 */
	public InstanceTracker(Class<?> cl){
		this.cl = cl;
		instances = new ArrayList<WeakReference<?>>();
	}
	
	/**
	 * Gets all created instances of the class in an {@link Object} array.
	 * @return an array of all created instances from the class.
	 */
	public Object[] getInstances(){
		Object[] objs = new Object[instances.size()];
		for (int i = 0; i < objs.length; i++) 
			objs[i] = instances.get(i).get();
		return objs;
	}
	
	/**
	 * Creates a new instance of the class, saves it as a {@link java.lang.ref.WeakReference} and returns it. 
	 * It is possible that an instance will not be created due to instantiation errors or access errors.
	 * 
	 * @return a new instance of the class if one could be created, null otherwise.
	 */
	public Object createInstance(){
		Object o = null;
		try {
			o = cl.newInstance(); 
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
		instances.add(new WeakReference<Object>(o));
		return o;
	}
}
