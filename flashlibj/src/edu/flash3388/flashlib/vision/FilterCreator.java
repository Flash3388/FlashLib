package edu.flash3388.flashlib.vision;

import java.util.Map;

/**
 * Filter creator allows for the dynamic creation of filters for vision processing. It converts from a name to
 * a class representing a filter and from a class to a name.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionFilter
 */
public interface FilterCreator {
	/**
	 * Creates a new filter by a given name
	 * @param name the name of the filter
	 * @return a new filter object
	 */
	VisionFilter create(String name);
	/**
	 * Gets the name used to represent the filter.
	 * 
	 * @param filter the filter
	 * @return the name of the filter
	 */
	String getSaveName(VisionFilter filter);
	/**
	 * Returns a map of the names of filters used for saving and creating and the classes which they instantiate.
	 * 
	 * @return a map object
	 */
	Map<String, Object> getFiltersMap();
}
