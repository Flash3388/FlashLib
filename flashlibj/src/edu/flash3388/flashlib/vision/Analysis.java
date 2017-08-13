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
	
	/**
	 * A double value. Indicates the target's center x coordinate.
	 */
	public static final String PROP_CENTER_X = "centerx";
	/**
	 * A double value. Indicates the target's center y coordinate.
	 */
	public static final String PROP_CENTER_Y = "centery";
	/**
	 * A double value. Indicates the x-axis distance in pixels between the center of the frame and the center of the target.
	 */
	public static final String PROP_HORIZONTAL_DISTANCE = "hor-dis";
	/**
	 * A double value. Indicates the y-axis distance in pixels between the center of the frame and the center of the target.
	 */
	public static final String PROP_VERTICAL_DISTANCE = "ver-dis";
	/**
	 * A double value. Indicates the distance to the target.
	 */
	public static final String PROP_TARGET_DISTANCE = "tar-dis";
	/**
	 * A double value. Indicates the angle offset between the center of the frame to the target center.
	 */
	public static final String PROP_ANGLE_OFFSET = "angle-offset";
	
	private final Map<String, Object> data = new HashMap<String, Object>();
	
	/**
	 * Sets an integer value for the analysis. This value can be retrieved using {@link #getInteger(String)} only.
	 * 
	 * @param key the key of the value which will be used to retrieve the value later
	 * @param value the value to set
	 */
	public void setInteger(String key, int value){
		data.put(key, value);
	}
	/**
	 * Sets a string value for the analysis. This value can be retrieved using {@link #getString(String)} only.
	 * 
	 * @param key the key of the value which will be used to retrieve the value later
	 * @param value the value to set
	 */
	public void setString(String key, String value){
		data.put(key, value);
	}
	/**
	 * Sets a double value for the analysis. This value can be retrieved using {@link #getDouble(String)} only.
	 * 
	 * @param key the key of the value which will be used to retrieve the value later
	 * @param value the value to set
	 */
	public void setDouble(String key, double value){
		data.put(key, value);
	}
	/**
	 * Sets an boolean value for the analysis. This value can be retrieved using {@link #getBoolean(String)} only.
	 * 
	 * @param key the key of the value which will be used to retrieve the value later
	 * @param value the value to set
	 */
	public void setBoolean(String key, boolean value){
		data.put(key, value);
	}
	
	/**
	 * Gets an integer value stored in the analysis. If the value with the given key is not an integer (i.e set using 
	 * {@link #setInteger(String, int)}) or a double (set using {@link #setDouble(String, double)}
	 *  an exception will be thrown. 
	 *  If the given key points to no value, 0 will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @return an integer stored with the given key
	 * @throws IllegalArgumentException if the key points to a non-number value (not set with {@link #setDouble(String, double)}
	 * or {@link #setInteger(String, int)}).
	 */
	public int getInteger(String key){
		return getInteger(key, 0);
	}
	/**
	 * Gets an integer value stored in the analysis. If the value with the given key is not an integer (i.e set using 
	 * {@link #setInteger(String, int)}) or a double (set using {@link #setDouble(String, double)}
	 *  an exception will be thrown. 
	 *  If the given key points to no value, the given default value will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @param def a default value to return
	 * @return an integer stored with the given key
	 * @throws IllegalArgumentException if the key points to a non-number value (not set with {@link #setDouble(String, double)}
	 * or {@link #setInteger(String, int)}).
	 */
	public int getInteger(String key, int def){
		Object o = data.get(key);
		if(o == null)
			return def;
		if(o instanceof Integer || o instanceof Double)
			return (int)o;
		throw new IllegalArgumentException("Value is not an int");
	}
	/**
	 * Gets a double value stored in the analysis. If the value with the given key is not an integer (i.e set using 
	 * {@link #setInteger(String, int)}) or a double (set using {@link #setDouble(String, double)}
	 *  an exception will be thrown. 
	 *  If the given key points to no value, 0.0 will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @return a double stored with the given key
	 * @throws IllegalArgumentException if the key points to a non-number value (not set with {@link #setDouble(String, double)}
	 * or {@link #setInteger(String, int)}).
	 */
	public double getDouble(String key){
		return getDouble(key, 0.0);
	}
	/**
	 * Gets a double value stored in the analysis. If the value with the given key is not an integer (i.e set using 
	 * {@link #setInteger(String, int)}) or a double (set using {@link #setDouble(String, double)}
	 *  an exception will be thrown. 
	 *  If the given key points to no value, the given default value will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @param def a default value to return
	 * @return a double stored with the given key
	 * @throws IllegalArgumentException if the key points to a non-number value (not set with {@link #setDouble(String, double)}
	 * or {@link #setInteger(String, int)}).
	 */
	public double getDouble(String key, double def){
		Object o = data.get(key);
		if(o == null)
			return def;
		if(o instanceof Double || o instanceof Integer)
			return (double)o;
		throw new IllegalArgumentException("Value is not a double");
	}
	/**
	 * Gets a string value stored in the analysis. If the value with the given key is not a string, it will be converted
	 * into one.
	 * If the given key points to no value, an empty string will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @return a string stored with the given key
	 */
	public String getString(String key){
		return getString(key, "");
	}
	/**
	 * Gets a string value stored in the analysis. If the value with the given key is not a string, it will be converted
	 * into one.
	 * If the given key points to no value, the given default value will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @param def a default value to return
	 * @return a string stored with the given key
	 */
	public String getString(String key, String def){
		Object o = data.get(key);
		if(o == null)
			return def;
		return o.toString();
	}
	/**
	 * Gets a double value stored in the analysis. If the value with the given key is not a boolean (i.e set using 
	 * {@link #setBoolean(String, boolean)}) an exception will be thrown. 
	 * If the given key points to no value, false will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @return a boolean stored with the given key
	 * @throws IllegalArgumentException if the key points to a value which is not boolean
	 */
	public boolean getBoolean(String key){
		return getBoolean(key, false);
	}
	/**
	 * Gets a double value stored in the analysis. If the value with the given key is not a boolean (i.e set using 
	 * {@link #setBoolean(String, boolean)}) an exception will be thrown. 
	 * If the given key points to no value, the given default value will be returned.
	 * 
	 * @param key the key of the value used when the value was set
	 * @param def a default value to return
	 * @return a boolean stored with the given key
	 * @throws IllegalArgumentException if the key points to a value which is not boolean
	 */
	public boolean getBoolean(String key, boolean def){
		Object o = data.get(key);
		if(o == null)
			return def;
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
