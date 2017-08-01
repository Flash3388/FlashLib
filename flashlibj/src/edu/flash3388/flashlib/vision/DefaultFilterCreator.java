package edu.flash3388.flashlib.vision;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * The default creator object for processing filters. Can load and create all basic filters provided with 
 * FlashLib.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see FilterCreator
 */
public class DefaultFilterCreator implements FilterCreator{

	private final Map<String, Object> map = 
			new HashMap<String, Object>();
	
	public DefaultFilterCreator() {
		map.put("highest", HighestFilter.class);
		map.put("lowest", LowestFilter.class);
		map.put("largest", LargestFilter.class);
		map.put("shape", ShapeFilter.class);
		map.put("ratio", RatioFilter.class);
		map.put("closest-left", ClosestToLeftFilter.class);
		map.put("closest-right", ClosestToRightFilter.class);
		map.put("closest-center", ClosestToCenterFilter.class);
		map.put("coordinate", CoordinateFilter.class);
		map.put("color", ColorFilter.class);
		map.put("gray", GrayFilter.class);
		map.put("circle", CircleFilter.class);
		map.put("template", TemplateFilter.class);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public VisionFilter create(String name) {
		Class<?> cl = (Class<?>)map.get(name);
		if(cl == null || !FlashUtil.isAssignable(cl, VisionFilter.class))
			return null;
		try {
			return (VisionFilter) cl.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSaveName(VisionFilter filter) {
		for (Iterator<Entry<String, Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> en = iterator.next();
			Class<?> cl = (Class<?>)en.getValue();
			if(cl == null || !FlashUtil.isAssignable(cl, VisionFilter.class))
				continue;
			if(cl == filter.getClass())
				return en.getKey();
		}
		return null;
	}

	@Override
	public Map<String, Object> getFiltersMap() {
		return map;
	}

}
