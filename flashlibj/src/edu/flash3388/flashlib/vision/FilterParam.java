package edu.flash3388.flashlib.vision;

public abstract class FilterParam {

	private int paramid;
	
	public int getID(){
		return paramid;
	}
	
	public abstract String getParamString();
	
	public static FilterParam createParam(int paramid, String param){
		return null;
	}
}
