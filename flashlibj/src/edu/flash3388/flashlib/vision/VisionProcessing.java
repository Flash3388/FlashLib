package edu.flash3388.flashlib.vision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.flash3388.flashlib.io.FileStream;
import edu.flash3388.flashlib.util.FlashUtil;

public class VisionProcessing {
	
	private List<ProcessingFilter> filters;
	
	public VisionProcessing(){
		filters = new ArrayList<ProcessingFilter>();
	}
	
	public void addFilter(ProcessingFilter filter){
		filters.add(filter);
	}
	public void addFilters(ProcessingFilter... filters){
		for (ProcessingFilter filter : filters)
			addFilter(filter);
	}
	public void removeFilter(ProcessingFilter filter){
		filters.remove(filter);
	}
	public void removeFilter(int idx){
		filters.remove(idx);
	}
	public ProcessingFilter getFilter(int index){
		return filters.get(index);
	}
	public ProcessingFilter[] getFilters(){
		return filters.toArray(new ProcessingFilter[0]);
	}
	
	public Analysis process(VisionSource source){
		for (ProcessingFilter filter : filters)
			filter.process(source);
		
		return source.getResult();
	}
	
	public void loadFilters(byte[] bytes){
		String filterstr = new String(bytes);
		String[] filters = filterstr.split("|");
		loadFilters(filters);
	}
	public void loadFilters(String file) throws NullPointerException, IOException{
		String[] filters = FileStream.readAllLines(file);
		loadFilters(filters);
	}
	public void loadFilters(String[] filters){
		for (int i = 0; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			int id = FlashUtil.toInt(splits[0]);
			double[] param;
			
			if(splits.length < 2)
				param = new double[0];
			else
				param = FlashUtil.toDoubleArray(Arrays.copyOfRange(splits, 1, splits.length));
			
			addFilter(ProcessingFilter.createFilter(id, param));
		}
	}
	
	public byte[] toBytes(){
		ProcessingFilter[] filters = getFilters();
		String filterstr = "";
		for (int i = 0; i < filters.length; i++) {
			int id = ProcessingFilter.getSaveId(filters[i]);
			String params = FlashUtil.toDataString(FlashUtil.toStringArray(filters[i].getParameters()), ":");
			filterstr += id + ":" + params + "|";
		}
		return filterstr.substring(0, filterstr.length()).getBytes();
	}
	public void saveToFile(String file){
		ProcessingFilter[] filters = getFilters();
		String[] filterstr = new String[filters.length];
		for (int i = 0; i < filters.length; i++) {
			int id = ProcessingFilter.getSaveId(filters[i]);
			String params = FlashUtil.toDataString(FlashUtil.toStringArray(filters[i].getParameters()), ":");
			filterstr[i] = id + ":" + params;
		}
		FileStream.writeLines(file, filterstr);
	}
	
	public static VisionProcessing createFromFile(String file){
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.loadFilters(file);
		} catch (NullPointerException | IOException e) {}
		return proc;
	}
	public static VisionProcessing createFromBytes(byte[] bytes){
		VisionProcessing proc = new VisionProcessing();
		proc.loadFilters(bytes);
		return proc;
	}
}
