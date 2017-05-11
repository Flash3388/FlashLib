package edu.flash3388.flashlib.vision;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
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
	
	public Analysis process(VisionSource source, BufferedImage img){
		return source.analyse(getFilters(), img);
	}
	
	public void loadFilters(byte[] bytes){
		String filterstr = new String(bytes);
		String[] filters = filterstr.split("|");
		for (int i = 0; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			int id = FlashUtil.toInt(splits[0]);
			FilterParam param = null;
			if(splits.length == 3)
				param = FilterParam.createParam(FlashUtil.toInt(splits[1]), splits[2]);
			
			addFilter(ProcessingFilter.createFilter(id, param));
		}
	}
	public void loadFilters(String file) throws NullPointerException, IOException{
		String[] filters = FileStream.readAllLines(file);
		for (int i = 0; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			int id = FlashUtil.toInt(splits[0]);
			FilterParam param = null;
			if(splits.length == 3)
				param = FilterParam.createParam(FlashUtil.toInt(splits[1]), splits[2]);
			
			addFilter(ProcessingFilter.createFilter(id, param));
		}
	}
	
	public byte[] toBytes(){
		ProcessingFilter[] filters = getFilters();
		String filterstr = "";
		for (int i = 0; i < filters.length; i++) {
			FilterParam params = filters[i].getFilterParameters();
			filterstr += filters[i].getID() + (params != null? ":"+params.getID()+":"+params.getParamString() : "")+"|";
		}
		return filterstr.substring(0, filterstr.length()-1).getBytes();
	}
	public void saveToFile(String file){
		ProcessingFilter[] filters = getFilters();
		String[] filterstr = new String[filters.length];
		for (int i = 0; i < filters.length; i++) {
			FilterParam params = filters[i].getFilterParameters();
			filterstr[i] = filters[i].getID() + (params != null? ":"+params.getID()+":"+params.getParamString() : "");
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
