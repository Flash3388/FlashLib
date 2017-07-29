package edu.flash3388.flashlib.vision;

import java.util.HashMap;
import java.util.Map;

import edu.flash3388.flashlib.util.FlashUtil;

/**
 * Represents a post vision analysis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Analysis {
	
	public static final String PROP_CENTER_X = "centerx";
	public static final String PROP_CENTER_Y = "centery";
	public static final String PROP_HORIZONTAL_DISTANCE = "hor-dis";
	public static final String PROP_VERTICAL_DISTANCE = "ver-dis";
	public static final String PROP_TARGET_DISTANCE = "tar-dis";
	public static final String PROP_ANGLE_OFFSET = "angle-offset";
	
	private final Map<String, Object> data = new HashMap<String, Object>();
	
	public void setInteger(String key, int value){
		data.put(key, value);
	}
	public void setString(String key, String value){
		data.put(key, value);
	}
	public void setDouble(String key, double value){
		data.put(key, value);
	}
	public void setBoolean(String key, boolean value){
		data.put(key, value);
	}
	
	public int getInteger(String key){
		Object o = data.get(key);
		if(o instanceof Integer || o instanceof Double)
			return (int)o;
		throw new IllegalArgumentException("Value is not an int");
	}
	public double getDouble(String key){
		Object o = data.get(key);
		if(o instanceof Double || o instanceof Integer)
			return (double)o;
		throw new IllegalArgumentException("Value is not a double");
	}
	public String getString(String key){
		Object o = data.get(key);
		return o.toString();
	}
	public boolean getBoolean(String key){
		Object o = data.get(key);
		if(o instanceof Boolean)
			return (boolean)o;
		throw new IllegalArgumentException("Value is not a boolean");
	}
	
	/**
	 * Creates a byte array and saves this analysis data into it.
	 * @return a byte array with data about this analysis
	 */
	public byte[] transmit(){
		String str = "";
		for (String key : data.keySet()) {
			Object val = data.get(key);
			String type = "";
			
			if(val instanceof Integer)
				type = "i";
			else if(val instanceof Double)
				type = "d";
			else if(val instanceof Boolean)
				type = "b";
			else type = "s";
			
			str += key + ":" + val + ":" + type + "|";
		}
		
		return str.getBytes();
	}
	
	/**
	 * Creates an analysis from a byte array. 
	 * @param bytes byte array with analysis data.
	 * @return a new analysis object
	 */
	public static Analysis fromBytes(byte[] bytes){
		Analysis an = new Analysis();
		
		String[] data = new String(bytes).split("\\|");
		for (String str : data) {
			String[] subdata = str.split(":");
			if(subdata.length != 3)
				continue;
			
			Object val;
			if(subdata[2].equals("i"))
				val = FlashUtil.toInt(subdata[1]);
			else if(subdata[2].equals("b"))
				val = FlashUtil.toBoolean(subdata[1]);
			else if(subdata[2].equals("d"))
				val = FlashUtil.toDouble(subdata[1]);
			else val = subdata[1];
			
			an.data.put(subdata[0], val);
		}
		
		return an;
	}
}
