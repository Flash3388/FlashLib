package edu.flash3388.flashlib.vision;

/**
 * FilterParam is a parameter used by filters to define their operation.
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
public abstract class FilterParam {
	
	/**
	 * Represent a filter parameter with double data. Could be used to define ratios and
	 * dimensions.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see FilterParam
	 */
	public static class DoubleParam extends FilterParam{

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
	 * Represent a filter parameter with int data. Could be used to define amounts.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see FilterParam
	 */
	public static class IntParam extends FilterParam{

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
	 * Represent a filter parameter with boolean data. Could be used to define if something is to be 
	 * executed or not.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see FilterParam
	 */
	public static class BooleanParam extends FilterParam{

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
	
	public FilterParam(String name){
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
	 * Gets the value of a parameter as a double value. If the filter is not an instance of {@link DoubleParam}, 0
	 * will be returned.
	 * @param param the filter param
	 * @return the double value, or 0 if the param is not an instance of {@link DoubleParam}
	 */
	public static double getDoubleValue(FilterParam param){
		if(param == null || !(param instanceof DoubleParam))
			return 0.0;
		return ((DoubleParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a int value. If the filter is not an instance of {@link IntParam}, 0
	 * will be returned.
	 * @param param the filter param
	 * @return the double value, or 0 if the param is not an instance of {@link IntParam}
	 */
	public static int getIntValue(FilterParam param){
		if(param == null || !(param instanceof IntParam))
			return 0;
		return ((IntParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a boolean value. If the filter is not an instance of {@link BooleanParam}, false
	 * will be returned.
	 * @param param the filter param
	 * @return the double value, or false if the param is not an instance of {@link BooleanParam}
	 */
	public static boolean getBooleanValue(FilterParam param){
		if(param == null || !(param instanceof BooleanParam))
			return false;
		return ((BooleanParam)param).value;
	}
	
	/**
	 * Creates a new filter parameter by name, type and value:
	 * <ul>
	 * 	<li>If the type is "int" an {@link IntParam} is returned</li>
	 * 	<li>If the type is "double" an {@link DoubleParam} is returned</li>
	 * 	<li>If the type is "boolean" an {@link BooleanParam} is returned</li>
	 * </ul>
	 * 
	 * @param name the name of the parameter
	 * @param type the type of the parameter
	 * @param value the value of the parameter
	 * @return a new filter parameter, or null if one could not be created
	 * @throws IllegalArgumentException if the name, type of value are null
	 * @throws RuntimeException if parsing the value to the appropriate type has failed
	 */
	public static FilterParam createParam(String name, String type, String value){
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
