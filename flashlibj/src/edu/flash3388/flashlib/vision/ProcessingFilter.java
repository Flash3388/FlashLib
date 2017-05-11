package edu.flash3388.flashlib.vision;

public abstract class ProcessingFilter {

	private int filterid;
	private FilterParam param;
	
	public ProcessingFilter(int id, FilterParam param){
		filterid = id;
		this.param = param;
	}
	public ProcessingFilter(int id){
		this(id, null);
	}
	
	public int getID(){
		return filterid;
	}
	public FilterParam getFilterParameters(){
		return param;
	}
	public void setFilterParameters(FilterParam param){
		this.param = param;
	}
	
	public abstract void process(VisionSource source);
	
	public static ProcessingFilter createFilter(int id, FilterParam param){
		return null;
	}
}
