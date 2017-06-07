package edu.flash3388.flashlib.vision;

import java.util.Map;

public abstract class ProcessingFilter {
	
	public abstract void process(VisionSource source);
	public abstract void parseParameters(Map<String, FilterParam> parameters);
	public abstract FilterParam[] getParameters();
	
	private static FilterCreator creator;
	
	public static void setFilterCreator(FilterCreator creator){
		ProcessingFilter.creator = creator;
	}
	public static boolean hasFilterCreator(){
		return creator != null;
	}
	
	public static ProcessingFilter createFilter(String name, Map<String, FilterParam> parameters){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		ProcessingFilter filter = creator.create(name);
		if(filter == null)
			return null;
		
		filter.parseParameters(parameters);
		return filter;
	}
	public static String getSaveName(ProcessingFilter filter){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		return creator.getSaveName(filter);
	}
}
