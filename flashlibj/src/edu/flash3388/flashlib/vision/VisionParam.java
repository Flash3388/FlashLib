package edu.flash3388.flashlib.vision;

/**
 * VisionParam is a parameter used by filters and analysis creators to define their operation.
 * There are 3 base types for parameters:
 * <ul>
 * 	<li>A double parameter {@link DoubleParam}</li>
 * 	<li>A boolean parameter {@link BooleanParam}</li>
 *  <li>An int parameter {@link IntParam}</li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see ProcessingFilter
 */
public abstract class VisionParam {
	
	/**
	 * Represent a parameter with double data. Could be used to define ratios and
	 * dimensions.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see VisionParam
	 */
	public static class DoubleParam extends VisionParam{

		private double value;
		
		public DoubleParam(String name, double value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Double";
		}
		
	}
	/**
	 * Represent a parameter with int data. Could be used to define amounts.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see VisionParam
	 */
	public static class IntParam extends VisionParam{

		private int value;
		
		public IntParam(String name, int value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Int";
		}
		
	}
	/**
	 * Represent a parameter with boolean data. Could be used to define if something is to be 
	 * executed or not.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see VisionParam
	 */
	public static class BooleanParam extends VisionParam{

		private boolean value;
		
		public BooleanParam(String name, boolean value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return String.valueOf(value);
		}
		@Override
		public String getType() {
			return "Boolean";
		}
		
	}
	
	private String name;
	
	public VisionParam(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the value of the parameter as a string.
	 * @return the value of the parameter
	 */
	public abstract String getValue();
	/**
	 * Gets the type of the parameter as a string. The type is used for saving the parameter and loading it.
	 * @return the value of the parameter
	 */
	public abstract String getType();
	
	/**
	 * Gets the value of a parameter as a double value. If the filter is not an instance of {@link DoubleParam}, a default value
	 * will be returned.
	 * @param param the param
	 * @param defaultVal the default value
	 * @return the double value, or defaultVal if the param is not an instance of {@link DoubleParam}
	 */
	public static double getDoubleValue(VisionParam param, double defaultVal){
		if(param == null || !(param instanceof DoubleParam))
			return defaultVal;
		return ((DoubleParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a double value. If the filter is not an instance of {@link DoubleParam}, 0
	 * will be returned.
	 * @param param the param
	 * @return the double value, or 0 if the param is not an instance of {@link DoubleParam}
	 */
	public static double getDoubleValue(VisionParam param){
		return getDoubleValue(param, 0.0);
	}
	
	/**
	 * Gets the value of a parameter as a int value. If the filter is not an instance of {@link IntParam}, a default value
	 * will be returned.
	 * @param param the param
	 * @param defaultVal the default value
	 * @return the double value, or defaultVal if the param is not an instance of {@link IntParam}
	 */
	public static int getIntValue(VisionParam param, int defaultVal){
		if(param == null || !(param instanceof IntParam))
			return defaultVal;
		return ((IntParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a int value. If the filter is not an instance of {@link IntParam}, 0
	 * will be returned.
	 * @param param the param
	 * @return the double value, or 0 if the param is not an instance of {@link IntParam}
	 */
	public static int getIntValue(VisionParam param){
		return getIntValue(param, 0);
	}
	
	/**
	 * Gets the value of a parameter as a boolean value. If the filter is not an instance of {@link BooleanParam}, the defualt value
	 * will be returned.
	 * @param param the param
	 * @param defaultVal the defualt value
	 * @return the double value, or defaultVal if the param is not an instance of {@link BooleanParam}
	 */
	public static boolean getBooleanValue(VisionParam param, boolean defaultVal){
		if(param == null || !(param instanceof BooleanParam))
			return defaultVal;
		return ((BooleanParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a boolean value. If the filter is not an instance of {@link BooleanParam}, false
	 * will be returned.
	 * @param param the param
	 * @return the double value, or false if the param is not an instance of {@link BooleanParam}
	 */
	public static boolean getBooleanValue(VisionParam param){
		return getBooleanValue(param, false);
	}
	
	/**
	 * Creates a new parameter by name, type and value:
	 * <ul>
	 * 	<li>If the type is "int" an {@link IntParam} is returned</li>
	 * 	<li>If the type is "double" an {@link DoubleParam} is returned</li>
	 * 	<li>If the type is "boolean" an {@link BooleanParam} is returned</li>
	 * </ul>
	 * 
	 * @param name the name of the parameter
	 * @param type the type of the parameter
	 * @param value the value of the parameter
	 * @return a new parameter, or null if one could not be created
	 * @throws IllegalArgumentException if the name, type of value are null
	 * @throws RuntimeException if parsing the value to the appropriate type has failed
	 */
	public static VisionParam createParam(String name, String type, String value){
		if(name == null)
			throw new IllegalArgumentException("Name value missing");
		if(value == null)
			throw new IllegalArgumentException("Value value missing: "+name);
		if(type == null)
			throw new IllegalArgumentException("Type value missing: "+name);
		
		if(type.equalsIgnoreCase("Int")){
			int val = 0;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to int: "+value);
			}
			return new IntParam(name, val);
		}
		if(type.equalsIgnoreCase("Double")){
			double val = 0;
			try {
				val = Double.parseDouble(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to double: "+value);
			}
			return new DoubleParam(name, val);
		}
		if(type.equalsIgnoreCase("Boolean")){
			boolean val = false;
			try {
				val = Boolean.parseBoolean(value);
			} catch (NumberFormatException e) {
				throw new RuntimeException("Could not parse param "+name+" to boolean: "+value);
			}
			return new BooleanParam(name, val);
		}
		return null;
	}
}
