package edu.flash3388.flashlib.math;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import edu.flash3388.flashlib.io.FileStream;
import edu.flash3388.flashlib.util.FlashUtil;

public abstract class Interpolation {

	private HashMap<Double, Double> map = new HashMap<Double, Double>();
	private double keyMargin;
	private double lastKey = 0;
	
	public Interpolation(double margin){
		this.keyMargin = margin;
	}
	
	public Map<Double, Double> getMap(){
		return map;
	}
	public void setKeyMargin(double keyMargin){
		this.keyMargin = keyMargin;
	}
	public double getKeyMargin(){
		return keyMargin;
	}
	public int getMappedValuesCount(){
		return map.size();
	}
	public void clear(){
		map.clear();
		lastKey = 0;
	}
	
	public double getValue(double key){
		Double val = map.get(key);
		if(val == null) return 0;
		return val;
	}
	public void put(double key, double value){
		map.put(key, value);
		lastKey = key;
	}
	public void putNext(double value){
		put(lastKey + keyMargin, value);
	}
	
	public void saveToFile(String file){
		Double[] keys = map.keySet().toArray(new Double[0]);
		Double[] values = map.values().toArray(new Double[0]);
		String[] lines = new String[keys.length];
		for (int i = 0; i < values.length; i++) 
			lines[i] = keys[i]+":"+values[i];
		FileStream.writeLines(file, lines);
	}
	public void readFromFile(String file){
		try {
			String[] lines = FileStream.readAllLines(file);
			for (int i = 0; i < lines.length; i++) {
				String[] split = lines[i].split(":");
				if(split.length != 2) continue;
				put(FlashUtil.toDouble(split[0]), FlashUtil.toDouble(split[1]));
			}
		} catch (NullPointerException | IOException e) {
		} 
	}
	
	public abstract double interpolate(double x);
}
