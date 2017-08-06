package edu.flash3388.flashlib.vision;

import java.util.Map;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * A base for filters used for vision processing. Each filter is used to filter data out of an image during processing. 
 * <p>
 * Filters are created dynamically through the use of a {@link FilterCreator}. A creator provides data for saving and loading
 * filters.
 * </p>
 * <p>
 * Most filters contain parameters which define certain aspects of the filter. 
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionProcessing
 * @see FilterCreator
 * @see VisionParam
 */
public abstract class VisionFilter {
	
	/**
	 * Processes data in an image from the vision source and filters out non matching data.
	 * @param source the source of the vision
	 */
	public abstract void process(VisionSource source);
	
	
	private static FilterCreator creator = new DefaultFilterCreator();
	
	/**
	 * Sets the filter creator used to save and load filters.
	 * @param creator the creator
	 */
	public static void setFilterCreator(FilterCreator creator){
		VisionFilter.creator = creator;
	}
	/**
	 * Gets whether or not a filter creator was set.
	 * @return true if a filter was set, false otherwise
	 */
	public static boolean hasFilterCreator(){
		return creator != null;
	}
	
	public static FilterCreator getFilterCreator(){
		return creator;
	}
	
	/**
	 * Creates a new filter by name and loads parameters into it. Uses the set filter creator to get the class
	 * representing the filter. If the filter creator was unable to create the filter, it is attempted to look at
	 * the name as a class name and create an instance of it. If this too fails, null returns.
	 * 
	 * @param name the name of the filter used by the filter creator
	 * @param parameters parameters for the filter
	 * @return the new filter, or null if one could not be created
	 * @throws IllegalStateException if a filter creator was not set
	 * @see FilterCreator#create(String)
	 */
	public static VisionFilter createFilter(String name, Map<String, VisionParam> parameters){
		if(creator == null)
			throw new IllegalStateException("Filter creator was not defined");
		
		VisionFilter filter = creator.create(name);
		if(filter == null){
			Object obj = FlashUtil.createInstance(name);
			if(obj == null || !(obj instanceof VisionFilter))
				return null;
			filter = (VisionFilter)filter;
		}
		
		//setting parameters
		if(parameters != null)
			VisionParam.setParameters(filter, parameters);
		
		return filter;
	}
	/**
	 * Gets the name of a filter according to the filter creator. If the creator was unable to
	 * provide a save name, the class name is used instead.
	 * 
	 * @param filter the filter
	 * @return the name of the filter, or null if not defined by the filter creator
	 * @see FilterCreator#getSaveName(VisionFilter)
	 */
	public static String getSaveName(VisionFilter filter){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		String name = creator.getSaveName(filter);
		if(name == null)
			name = filter.getClass().getName();
		return name;
	}
}
