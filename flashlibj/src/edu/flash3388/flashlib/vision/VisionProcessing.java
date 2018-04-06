package edu.flash3388.flashlib.vision;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * VisionProcessing is the basis for the dynamic vision system. It provides you with the ability to load different
 * vision filters that will be used when running the vision runner. It is possible to use different libraries for vision.
 * The library needs to have a class which implements {@link VisionSource}.
 * <p>
 * Vision processing is done by using filters to filter out data from images which are stored in a vision source. All filters
 * are used one by one. At the end enough data should be filtered out for the vision source to identify only specific contours
 * in the image which are the objects we are looking for.
 * </p>
 * <p>
 * By default, analysis data is returned from {@link VisionSource#getResult()} which provides simple data.
 * But it is possible to use an {@link AnalysisCreator} to provide more data from vision.
 * </p>
 * <p>
 * Data can be loaded from and saved to XML files or bytes, for transfer of processing data.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see VisionFilter
 * @see VisionSource
 * @see AnalysisCreator
 */
public final class VisionProcessing implements Serializable {
	
	private List<VisionFilter> filters;
	private AnalysisCreator analysisCreator;
	private String name;
	
	/**
	 * Creates a new vision processing objects. 
	 * @param name the name of the processing
	 */
	public VisionProcessing(String name){
		filters = new ArrayList<VisionFilter>();
	}
	public VisionProcessing(){
		this("");
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the {@link AnalysisCreator} object used by this processing to create the appropriate
	 * {@link Analysis} from the vision. If the creator is null, {@link VisionSource#getResult()} will
	 * be used to get the {@link Analysis}.
	 * @param creator the creator object
	 */
	public void setAnalysisCreator(AnalysisCreator creator){
		this.analysisCreator = creator;
	}
	/**
	 * Gets the {@link AnalysisCreator} object used by this processing to create the appropriate
	 * {@link Analysis} from the vision. If the creator is null, {@link VisionSource#getResult()} will
	 * be used to get the {@link Analysis}.
	 * @return the creator object, or null if it not set
	 */
	public AnalysisCreator getAnalysisCreator(){
		return analysisCreator;
	}
	
	/**
	 * Adds a new filter to the filter list. When processing an image with this processing, this filter will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filter the filter to be used
	 */
	public void addFilter(VisionFilter filter){
		filters.add(filter);
	}
	/**
	 * Adds new filters to the filter list. When processing an image with this processing, those filters will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filters the filter to be used
	 */
	public void addFilters(VisionFilter... filters){
		for (VisionFilter filter : filters)
			addFilter(filter);
	}
	/**
	 * Removes a filter from the filter list. The removed filter will no longer be used when analyzing images.
	 * @param filter filter to remove
	 */
	public void removeFilter(VisionFilter filter){
		filters.remove(filter);
	}
	/**
	 * Removes a filter from the given index. The removed filter will no longer be used when analyzing images. If no filter
	 * exists at that index, nothing will happen.
	 * @param idx index of filter to remove
	 */
	public void removeFilter(int idx){
		if(idx < 0 || idx >= filters.size())
			return;
		filters.remove(idx);
	}
	/**
	 * Gets a filter at the given index. If no filter exists at that index, null will be returned.
	 * 
	 * @param index the index 
	 * @return a filter at that index, or null if none exist.
	 */
	public VisionFilter getFilter(int index){
		if(index < 0 || index >= filters.size())
			return null;
		return filters.get(index);
	}
	/**
	 * Gets all the filters as an array.
	 * @return all the filters
	 */
	public VisionFilter[] getFilters(){
		return filters.toArray(new VisionFilter[filters.size()]);
	}
	
	/**
	 * Processes all filters using a given vision source. Calls {@link VisionFilter#process(VisionSource)}
	 * for all the filters.
	 * @param source the vision source for analyzing.
	 */
	public void process(VisionSource source){
		for (VisionFilter filter : filters)
			filter.process(source);
	}
	/**
	 * Processes all filters using a given vision source and returns an {@link Analysis} object which contains the
	 * result. The result is received from the {@link AnalysisCreator} object set to this processing, or if no creator
	 * is set, {@link VisionSource#getResult()} is used. 
	 * Calls {@link VisionFilter#process(VisionSource)} for all the filters.
	 * @param source the vision source for analyzing.
	 * @return the result of the processing
	 */
	public Analysis processAndGet(VisionSource source){
		process(source);
		return analysisCreator != null? analysisCreator.createAnalysis(source) : 
			source.getResult();
	}
	/**
	 * Processes all filters using a given vision source and returns the result of {@link VisionSource#getResults()}. 
	 * Calls {@link VisionFilter#process(VisionSource)} for all the filters.
	 * @param source the vision source for analyzing.
	 * @return the result of the processing
	 */
	public Analysis[] processAndGetAll(VisionSource source){
		process(source);
		return source.getResults();
	}
}
