package edu.flash3388.flashlib.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import edu.flash3388.flashlib.io.FileReader;
import edu.flash3388.flashlib.io.FileStream;

public class Properties {

	public static enum MapType{
		Hash, ConcurrentHash, Tree
	}
	
	private Map<String, String> properties;
	
	public Properties(){
		this(MapType.Hash);
	}
	public Properties(MapType type){
		switch(type){
			case ConcurrentHash:
				properties = new ConcurrentHashMap<String, String>();
				break;
			case Tree:
				properties = new TreeMap<String, String>();
				break;
			default:
				properties = new HashMap<String, String>();
				break;
		}
	}
	
	public Map<String, String> getMap(){
		return properties;
	}
	public Set<String> getKeySet(){
		return properties.keySet();
	}
	public Collection<String> getValuesCollection(){
		return properties.values();
	}
	public String[] keys(){
		return getKeySet().toArray(new String[0]);
	}
	public String[] values(){
		return getValuesCollection().toArray(new String[0]);
	}
	
	public boolean hasProperty(String prop){
		return getProperty(prop) != null;
	}
	public String getProperty(String prop){
		return properties.get(prop);
	}
	public double getNumberProperty(String prop){
		String val = getProperty(prop);
		if(val == null) return 0;
		return FlashUtil.toDouble(val);
	}
	public int getIntegerProperty(String prop){
		String val = getProperty(prop);
		if(val == null) return 0;
		return FlashUtil.toInt(val);
	}
	public boolean getBooleanProperty(String prop){
		String val = getProperty(prop);
		if(val == null || !val.equalsIgnoreCase("true"))
			return false;
		return true;
	}
	
	public void putProperty(String prop, String val){
		properties.put(prop, val);
	}
	public void putNumberProperty(String prop, double val){
		putProperty(prop, String.valueOf(val));
	}
	public void putIntegerProperty(String prop, int val){
		putProperty(prop, String.valueOf(val));
	}
	public void putBooleanProperty(String prop, boolean val){
		putProperty(prop, val?"TRUE":"FALSE");
	}
	
	public void saveToFile(String file){
		savePropertiesToFile(this, file);
	}
	public void loadFromFile(String file){
		loadPropertiesFromFile(this, file);
	}
	
	public static void savePropertiesToFile(Properties props, String file){
		String[] keys = props.keys(),
				 values = props.values(),
				 lines = new String[keys.length];
		for (int i = 0; i < lines.length; i++)
			lines[i] = keys[i] + FileReader.DEFAULT_CONSTANT_SEPERATOR + values[i];
		FileStream.writeLines(file, lines);
	}
	public static void loadPropertiesFromFile(Properties props, String file){
		if(props == null) return;
		try {
			String[] lines = FileStream.readAllLines(file);
			for (String line : lines) {
				if(line == null) continue;
				String[] splits = line.split(FileReader.DEFAULT_CONSTANT_SEPERATOR);
				if(splits.length == 2)
					props.putProperty(splits[0], splits[1]);
			}
		} catch (NullPointerException | IOException e) {
		}
	}
	public static Properties loadPropertiesFromFile(String file){
		Properties props = new Properties();
		loadPropertiesFromFile(props, file);
		return props;
	}
}
