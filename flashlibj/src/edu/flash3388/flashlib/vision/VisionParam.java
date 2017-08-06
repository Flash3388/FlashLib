package edu.flash3388.flashlib.vision;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.Property;

/**
 * VisionParam is a parameter used by filters and analysis creators to define their operation.
 * There are 3 base types for parameters:
 * <ul>
 * 	<li>A double parameter {@link DoubleParam}</li>
 * 	<li>A boolean parameter {@link BooleanParam}</li>
 *  <li>An int parameter {@link IntParam}</li>
 *  <li>A string parameter {@link StringParam}</li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see VisionFilter
 */
public abstract class VisionParam {
	
	/**
	 * Represent a parameter with string data.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.0.0
	 * @see VisionParam
	 */
	public static class StringParam extends VisionParam{

		private String value;
		
		public StringParam(String name, String value) {
			super(name);
			this.value = value;
		}

		@Override
		public String getValue() {
			return value;
		}
		@Override
		public String getType() {
			return "String";
		}
		
	}
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
	 * Gets the value of a parameter as a string value. If the filter is not an instance of {@link StringParam}, the defualt value
	 * will be returned.
	 * @param param the param
	 * @param defaultVal the defualt value
	 * @return the double value, or defaultVal if the param is not an instance of {@link StringParam}
	 */
	public static String getStringValue(VisionParam param, String defaultVal){
		if(param == null || !(param instanceof StringParam))
			return defaultVal;
		return ((StringParam)param).value;
	}
	/**
	 * Gets the value of a parameter as a string value. If the filter is not an instance of {@link StringParam}, an empty string
	 * will be returned.
	 * @param param the param
	 * @return the double value, or an empty if the param is not an instance of {@link StringParam}
	 */
	public static String getStringValue(VisionParam param){
		return getStringValue(param, "");
	}
	
	/**
	 * Sets property data of a method which returns a bean property with a given vision param value. 
	 * If the property type does not match the given param type, nothing is done.
	 * 
	 * @param method the method to call
	 * @param param the param to set data from
	 * @param instance instance of the class containing the property to use
	 */
	@SuppressWarnings("unchecked")
	public static void setValueForMethod(Method method, VisionParam param, Object instance){
		Class<?> retType = method.getReturnType();
		if(retType == null)
			return;
		
		Object obj = null;
		try {
			obj = method.invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
		if(obj == null || !(FlashUtil.isAssignable(retType, Property.class)))
			return;
		
		Type[] genericTypes = FlashUtil.findGenericArgumentsOfSuperType(retType, Property.class);
		if(genericTypes == null || genericTypes.length < 1 || genericTypes[0] instanceof ParameterizedType)
			return;
		
		Property<?> prop = (Property<?>)obj;
		Class<?> propType = (Class<?>)genericTypes[0];
		if(FlashUtil.isAssignable(propType, Boolean.class) && param instanceof BooleanParam){
			((Property<Boolean>)prop).setValue(getBooleanValue(param));
		}
		else if(FlashUtil.isAssignable(propType, Double.class) && param instanceof DoubleParam){
			((Property<Double>)prop).setValue(getDoubleValue(param));
		}
		else if(FlashUtil.isAssignable(propType, Integer.class) && param instanceof IntParam){
			((Property<Integer>)obj).setValue(getIntValue(param));
		}
		else if(FlashUtil.isAssignable(propType, String.class) && param instanceof StringParam){
			((Property<String>)obj).setValue(getStringValue(param));
		}
	}
	/**
	 * Gets a VisionParam from a method which should return a property.
	 * 
	 * @param method the method to get data from
	 * @param instance instance which contains the method
	 * @param name name of the property
	 * @return a new vision parameter with the data from the method, or null if unable to get data
	 */
	public static VisionParam getValueFromMethod(Method method, Object instance, String name){
		Class<?> retType = method.getReturnType();
		if(retType == null)
			return null;
		
		Object obj = null;
		try {
			obj = method.invoke(instance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
		if(obj == null || !(FlashUtil.isAssignable(retType, Property.class)))
			return null;
		
		Type[] genericTypes = FlashUtil.findGenericArgumentsOfSuperType(retType, Property.class);
		if(genericTypes == null || genericTypes.length < 1 || genericTypes[0] instanceof ParameterizedType)
			return null;
		
		Property<?> prop = (Property<?>)obj;
		Class<?> propType = (Class<?>)genericTypes[0];
		if(FlashUtil.isAssignable(propType, Boolean.class)){
			boolean val = (Boolean)prop.getValue();
			return new BooleanParam(name, val);
		}
		else if(FlashUtil.isAssignable(propType, Double.class)){
			double val = (Double)prop.getValue();
			return new DoubleParam(name, val);
		}
		else if(FlashUtil.isAssignable(propType, Integer.class)){
			int val = (Integer)prop.getValue();
			return new IntParam(name, val);
		}
		else if(FlashUtil.isAssignable(propType, String.class)){
			String val = (String)prop.getValue();
			return new StringParam(name, val);
		}
		
		return null;
	}
	/**
	 * Gets the parameters of a creator by searching for methods which end with the name "Property" and
	 * return a type of bean (DoubleProperty, IntegerProperty, Property, BooleanProperty).
	 * @param instance object to get parameters for.
	 *
	 * @return an array of parameters.
	 */
	public static VisionParam[] getParameters(Object instance){
		List<VisionParam> parameters = new ArrayList<VisionParam>();
		Map<String, Method> methods = findParametersMethods(instance.getClass());
		for (Iterator<Entry<String, Method>> entries = methods.entrySet().iterator(); entries.hasNext();) {
			Entry<String, Method> entry = entries.next();
			String name = entry.getKey();
			Method method = entry.getValue();
			
			VisionParam param = VisionParam.getValueFromMethod(method, instance, name);
			if(param != null)
				parameters.add(param);
		}
		return parameters.toArray(new VisionParam[parameters.size()]);
	}
	/**
	 * Sets the value of properties with an instance in accordance to values of given
	 * parameters.
	 * 
	 * @param instance the instance to set data for
	 * @param parameters the parameters to set
	 */
	public static void setParameters(Object instance, Map<String, VisionParam> parameters){
		Map<String, Method> methods = findParametersMethods(instance.getClass());
		for (Iterator<Entry<String, Method>> entries = methods.entrySet().iterator(); entries.hasNext();) {
			Entry<String, Method> entry = entries.next();
			String name = entry.getKey();
			Method method = entry.getValue();
			
			VisionParam param = parameters.get(name);
			if(param != null)
				VisionParam.setValueForMethod(method, param, instance);
		}
	}
	/**
	 * Gets all the methods in the given class which are used to get properties according to
	 * the convention used for vision parameters.
	 * 
	 * @param cl the class to search
	 * @return a map of methods as values and property names as keys
	 */
	public static Map<String, Method> findParametersMethods(Class<?> cl){
		Map<String, Method> methodMap = new LinkedHashMap<String, Method>();
		Method[] methods = cl.getMethods();
		for (Method method : methods) {
			int idx = method.getName().indexOf("Property");
			if(idx >= 0 && idx + 8 == method.getName().length()){
				String propName = method.getName().substring(0, idx);
				methodMap.put(propName, method);
			}
		}
		return methodMap;
	}
	
	/**
	 * Creates a new parameter by name, type and value:
	 * <ul>
	 * 	<li>If the type is "int" an {@link IntParam} is returned</li>
	 * 	<li>If the type is "double" an {@link DoubleParam} is returned</li>
	 * 	<li>If the type is "boolean" an {@link BooleanParam} is returned</li>
	 * 	<li>If the type is "string" an {@link StringParam} is returned</li>
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
		if(type.equalsIgnoreCase("String")){
			return new StringParam(name, value);
		}
		
		return null;
	}
}
