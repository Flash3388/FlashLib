package edu.flash3388.flashlib.util;

import java.util.HashMap;
import java.util.Map;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.devices.StringDataSource;

public class ConstantsHandler {
	
	private static Map<String, DoubleDataSource.VarDataSource> doubleMap = 
			new HashMap<String, DoubleDataSource.VarDataSource>();
	private static Map<String, StringDataSource.VarDataSource> stringMap = 
			new HashMap<String, StringDataSource.VarDataSource>();
	
	public static DoubleDataSource putNumber(String name, double iniVal){
		DoubleDataSource.VarDataSource source = doubleMap.get(name);
		if(source == null){
			source = new DoubleDataSource.VarDataSource(iniVal);
			doubleMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	public static DoubleDataSource getNumber(String name){
		return doubleMap.get(name);
	}
	
	public static StringDataSource putString(String name, String iniVal){
		StringDataSource.VarDataSource source = stringMap.get(name);
		if(source == null){
			source = new StringDataSource.VarDataSource(iniVal);
			stringMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	public static StringDataSource getString(String name){
		return stringMap.get(name);
	}
}
