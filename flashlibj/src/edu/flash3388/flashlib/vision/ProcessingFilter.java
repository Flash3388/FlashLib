package edu.flash3388.flashlib.vision;

public abstract class ProcessingFilter {

	public abstract void process(VisionSource source);
	public abstract void parseParameters(double[] parameters);
	public abstract double[] getParameters();
	
	private static FilterCreator creator;
	
	public static void setFilterCreator(FilterCreator creator){
		ProcessingFilter.creator = creator;
	}
	public static boolean hasFilterCreator(){
		return creator != null;
	}
	
	public static ProcessingFilter createFilter(int id, double[] parameters){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		ProcessingFilter filter = creator.create(id);
		filter.parseParameters(parameters);
		return filter;
	}
	public static byte getSaveId(ProcessingFilter filter){
		if(creator == null)
			throw new IllegalStateException("Filter creator was now defined");
		
		return creator.getSaveId(filter);
	}
}
