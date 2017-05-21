package edu.flash3388.flashlib.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class InstanceTracker {
	
	private List<WeakReference<?>> instances; 
	private Class<?> cl;
	
	public InstanceTracker(Class<?> cl){
		this.cl = cl;
		instances = new ArrayList<WeakReference<?>>();
	}
	
	public Object[] getInstances(){
		Object[] objs = new Object[instances.size()];
		for (int i = 0; i < objs.length; i++) 
			objs[i] = instances.get(i).get();
		return objs;
	}
	
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
