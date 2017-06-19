package edu.flash3388.flashlib.vision;

import java.util.Map;

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
 * @see FilterParam
 */
public abstract class ProcessingFilter {
	
	/**
	 * Processes data in an image from the vision source and filters out non matching data.
	 * @param source the source of the vision
	 */
	public abstract void process(VisionSource source);
	/**
	 * Loads parameters for the filter. Used when loading the filter from a file or a byte stream.
	 * @param parameters a map of parameters where the key is the name.
	 */
	public abstract void parseParameters(Map<String, FilterParam> parameters);
	/**
	 * Gets the parameters of the filter. Used mostly when saving the filter into a file or a byte stream.
	 * @return an array of parameters
	 */
	public abstract FilterParam[] getParameters();
	
	private static FilterCreator creator;
	
	/**
	 * Sets the filter creator used to save and load filters.
	 * @param creator the creator
	 */
	public static void setFilterCreator(FilterCreator creator){
		ProcessingFilter.creator = creator;
	}
	/**
	 * Gets whether or not a filter creator was set.
	 * @return true if a filter was set, false otherwise
	 */
	public static boolean hasFilterCreator(){
		return creator != null;
	}
	
	/**
	 * Creates a new filter by name and loads parameters into it. Uses the set filter creator to get the class
	 * representing the filter.
	 * 
	 * @param name the name of the filter used by the filter creator
	 * @param parameters parameters for the filter
	 * @return the new filter, or null if one could not be created
	 * @throws IllegalStateException if a filter creator was not set
	 * @see FilterCreator#create(String)
	 */
	public static ProcessingFilter createFilter(String name, Map<String, FilterParam> parameters){
		if(creator == null)
			throw new IllegalStateException("Filter creator was not defined");
		
		ProcessingFilter filter = creator.create(name);
		if(filter == null)
			return null;
		
		filter.parseParameters(parameters);
		return filter;
	}
	/**
	 * Gets the name of a filter according to the filter creator.
	 * 
	 * @param filter the filter
	 * @return the name of the filter, or null if not defined by the filter creator
	 * @see FilterCreator#getSaveName(ProcessingFilter)
	 */
	public static String getSaveName(ProcessingFilter filter){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		return creator.getSaveName(filter);
	}
}
