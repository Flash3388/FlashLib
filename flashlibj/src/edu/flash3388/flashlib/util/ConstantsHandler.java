package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.flash3388.flashlib.io.FileStream;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.Property;
import edu.flash3388.flashlib.util.beans.SimpleBooleanProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleProperty;

/**
 * 
 * Provides a platform for saving and handling of variables of double, string and boolean types. Variables are saved
 * in a {@link java.util.HashMap} by their types and a string value which represents a name. 
 * <p>
 * Values are wrapped in data source objects which allows updating of values in real time:
 * <ul>
 * 		<li> double: {@link DoubleProperty} </li>
 * 		<li> string: {@link Property} </li>
 * 		<li> boolean: {@link BooleanProperty} </li>
 * </ul>
 * <p>
 * It is possible to save and load values from XML files using the XML DOM parser {@link org.w3c.dom}.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class ConstantsHandler {
	
	private ConstantsHandler(){}
	
	private static final Map<String, DoubleProperty> doubleMap = 
			new HashMap<String, DoubleProperty>();
	private static final Map<String, Property<String>> stringMap = 
			new HashMap<String, Property<String>>();
	private static final Map<String, BooleanProperty> booleanMap = 
			new HashMap<String, BooleanProperty>();
	
	//--------------------------------------------------------------------
	//-----------------------General--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Clears all values in the data maps.
	 */
	public static void clear(){
		doubleMap.clear();
		stringMap.clear();
		booleanMap.clear();
	}
	/**
	 * Gets the total count of values in the maps of all types.
	 * @return the count of values in the maps
	 */
	public static int getTotalMapCount(){
		return getBooleanMapCount() + getNumberMapCount() + getStringMapCount();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Numbers--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets the count of number values in the number map.
	 * 
	 * @return the amount of number values
	 */
	public static int getNumberMapCount(){
		return doubleMap.size();
	}
	/**
	 * Gets and array of the keys of number values from the number map.
	 * @return a string array of keys for the number values.
	 */
	public static String[] getNumberMapNames(){
		String[] keys = new String[doubleMap.size()];
		return doubleMap.keySet().toArray(keys);
	}
	/**
	 * Removes a number value represented by a given key from the numbers map.
	 * 
	 * @param name the key representing the value in the map
	 * @return the {@link DoubleProperty} wrapper of the value
	 */
	public static DoubleProperty removeNumber(String name){
		return doubleMap.remove(name);
	}
	/**
	 * Adds a new value represented by a given key with an initial value into the number map. If the key already exists in the
	 * map the value is not overridden.
	 * 
	 * @param name the key of the new value
	 * @param iniVal the initial value
	 * @return a {@link DoubleProperty} wrapper for the value. If the key exists in the map, an existing wrapper is returned.
	 */
	public static DoubleProperty addNumber(String name, double iniVal){
		DoubleProperty source = doubleMap.get(name);
		if(source == null){
			source = new SimpleDoubleProperty(iniVal);
			doubleMap.put(name, source);
		}
		return source;
	}
	/**
	 * Puts a new value into the wrapper represented by a given key in the map. If the key does not exist in the map, a new
	 * wrapper is created with the given value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal the new value
	 * @return a {@link DoubleProperty} wrapper for the value represented by the key. If the key does not exists in the map, an new wrapper is created.
	 */
	public static DoubleProperty putNumber(String name, double iniVal){
		DoubleProperty source = doubleMap.get(name);
		if(source == null){
			source = new SimpleDoubleProperty(iniVal);
			doubleMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	/**
	 * Gets the {@link DoubleProperty} wrapper represented by a given key in the map. If such a key does not exist in the map,
	 * a new wrapper is created with a given initial value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal a initial value in case the key does not exist
	 * @return the {@link DoubleProperty} wrapper represented by a given key in the map
	 */
	public static DoubleProperty getNumber(String name, double iniVal){
		DoubleProperty source = doubleMap.get(name);
		if(source == null){
			source = new SimpleDoubleProperty(iniVal);
			doubleMap.put(name, source);
		}
		return source;
	}
	/**
	 * Gets the {@link DoubleProperty} wrapper represented by a given key in the map. If the key does not exist in the map,
	 * null is returned.
	 * 
	 * @param name the key of the wrapper
	 * @return the {@link DoubleProperty} wrapper represented by a given key in the map
	 */
	public static DoubleProperty getNumber(String name){
		return doubleMap.get(name);
	}
	/**
	 * Gets whether the key exists in the map.
	 * 
	 * @param name the key
	 * @return True if the key exists in the map, false otherwise.
	 */
	public static boolean hasNumber(String name){
		return doubleMap.containsKey(name);
	}
	/**
	 * Gets a double value from the wrapper represented by a given key. If the key does not exist, 0 is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @return double value saved in the wrapper, 0 if the key does not exist.
	 */
	public static double getNumberValue(String name){
		return getNumberValue(name, 0.0);
	}
	/**
	 * Gets a double value from the wrapper represented by a given key. If the key does not exist, an initial value is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @param iniVal a value to return if the key does not exist
	 * @return double value saved in the wrapper, an initial value if the key does not exist.
	 */
	public static double getNumberValue(String name, double iniVal){
		DoubleProperty source = doubleMap.get(name);
		if(source == null)
			return iniVal;
		return source.get();
	}
	/**
	 * Gets a integer value from the wrapper represented by a given key. If the key does not exist, 0 is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @return integer value saved in the wrapper, 0 if the key does not exist.
	 */
	public static int getIntegerValue(String name){
		return getIntegerValue(name, 0);
	}
	/**
	 * Gets an integer value from the wrapper represented by a given key. If the key does not exist, an initial value is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @param iniVal a value to return if the key does not exist
	 * @return integer value saved in the wrapper, an initial value if the key does not exist.
	 */
	public static int getIntegerValue(String name, int iniVal){
		DoubleProperty source = doubleMap.get(name);
		if(source == null)
			return iniVal;
		return (int) source.get();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Strings--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets the count of string values in the string map.
	 * 
	 * @return the amount of string values
	 */
	public static int getStringMapCount(){
		return stringMap.size();
	}
	/**
	 * Gets and array of the keys of string values from the string map.
	 * @return a string array of keys for the string values.
	 */
	public static String[] getStringMapNames(){
		String[] keys = new String[stringMap.size()];
		return stringMap.keySet().toArray(keys);
	}
	/**
	 * Removes a string value represented by a given key from the strings map.
	 * 
	 * @param name the key representing the value in the map
	 * @return the {@link Property} wrapper of the value
	 */
	public static Property<String> removeString(String name){
		return stringMap.remove(name);
	}
	/**
	 * Adds a new value represented by a given key with an initial value into the number map. If the key already exists in the
	 * map the value is not overridden.
	 * 
	 * @param name the key of the new value
	 * @param iniVal the initial value
	 * @return a {@link Property} wrapper for the value. If the key exists in the map, an existing wrapper is returned.
	 */
	public static Property<String> addString(String name, String iniVal){
		Property<String> source = stringMap.get(name);
		if(source == null){
			source = new SimpleProperty<String>(iniVal);
			stringMap.put(name, source);
		}
		return source;
	}
	/**
	 * Puts a new value into the wrapper represented by a given key in the map. If the key does not exist in the map, a new
	 * wrapper is created with the given value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal the new value
	 * @return a {@link Property} wrapper for the value represented by the key. If the key does not exists in the map, an new wrapper is created.
	 */
	public static Property<String> putString(String name, String iniVal){
		Property<String> source = stringMap.get(name);
		if(source == null){
			source = new SimpleProperty<String>(iniVal);
			stringMap.put(name, source);
		}else source.setValue(iniVal);
		
		return source;
	}
	/**
	 * Gets the {@link Property} wrapper represented by a given key in the map. If such a key does not exist in the map,
	 * a new wrapper is created with a given initial value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal a initial value in case the key does not exist
	 * @return the {@link Property} wrapper represented by a given key in the map
	 */
	public static Property<String> getString(String name, String iniVal){
		Property<String> source = stringMap.get(name);
		if(source == null){
			source = new SimpleProperty<String>(iniVal);
			stringMap.put(name, source);
		}
		return source;
	}
	/**
	 * Gets the {@link Property} wrapper represented by a given key in the map. If the key does not exist in the map,
	 * null is returned.
	 * 
	 * @param name the key of the wrapper
	 * @return the {@link Property} wrapper represented by a given key in the map
	 */
	public static Property<String> getString(String name){
		return stringMap.get(name);
	}
	/**
	 * Gets whether the key exists in the map.
	 * 
	 * @param name the key
	 * @return True if the key exists in the map, false otherwise.
	 */
	public static boolean hasString(String name){
		return stringMap.containsKey(name);
	}
	/**
	 * Gets a string value from the wrapper represented by a given key. If the key does not exist, null is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @return string value saved in the wrapper, null if the key does not exist.
	 */
	public static String getStringValue(String name){
		return getStringValue(name, null);
	}
	/**
	 * Gets a string value from the wrapper represented by a given key. If the key does not exist, an initial value is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @param iniVal a value to return if the key does not exist
	 * @return string value saved in the wrapper, an initial value if the key does not exist.
	 */
	public static String getStringValue(String name, String iniVal){
		Property<String> source = stringMap.get(name);
		if(source == null)
			return iniVal;
		return source.getValue();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Boolean--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets the count of boolean values in the boolean map.
	 * 
	 * @return the amount of boolean values
	 */
	public static int getBooleanMapCount(){
		return booleanMap.size();
	}
	/**
	 * Gets and array of the keys of string values from the boolean map.
	 * @return a string array of keys for the boolean values.
	 */
	public static String[] getBooleanMapNames(){
		String[] keys = new String[booleanMap.size()];
		return booleanMap.keySet().toArray(keys);
	}
	/**
	 * Removes a string value represented by a given key from the boolean map.
	 * 
	 * @param name the key representing the value in the map
	 * @return the {@link BooleanProperty} wrapper of the value
	 */
	public static BooleanProperty removeBoolean(String name){
		return booleanMap.remove(name);
	}
	/**
	 * Adds a new value represented by a given key with an initial value into the number map. If the key already exists in the
	 * map the value is not overridden.
	 * 
	 * @param name the key of the new value
	 * @param iniVal the initial value
	 * @return a {@link BooleanProperty} wrapper for the value. If the key exists in the map, an existing wrapper is returned.
	 */
	public static BooleanProperty addBoolean(String name, boolean iniVal){
		BooleanProperty source = booleanMap.get(name);
		if(source == null){
			source = new SimpleBooleanProperty(iniVal);
			booleanMap.put(name, source);
		}
		return source;
	}
	/**
	 * Puts a new value into the wrapper represented by a given key in the map. If the key does not exist in the map, a new
	 * wrapper is created with the given value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal the new value
	 * @return a {@link BooleanProperty} wrapper for the value represented by the key. If the key does not exists in the map, an new wrapper is created.
	 */
	public static BooleanProperty putBoolean(String name, boolean iniVal){
		BooleanProperty source = booleanMap.get(name);
		if(source == null){
			source = new SimpleBooleanProperty(iniVal);
			booleanMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	/**
	 * Gets the {@link BooleanProperty} wrapper represented by a given key in the map. If such a key does not exist in the map,
	 * a new wrapper is created with a given initial value.
	 * 
	 * @param name the key of the wrapper
	 * @param iniVal a initial value in case the key does not exist
	 * @return the {@link BooleanProperty} wrapper represented by a given key in the map
	 */
	public static BooleanProperty getBoolean(String name, boolean iniVal){
		BooleanProperty source = booleanMap.get(name);
		if(source == null){
			source = new SimpleBooleanProperty(iniVal);
			booleanMap.put(name, source);
		}
		return source;
	}
	/**
	 * Gets the {@link BooleanProperty} wrapper represented by a given key in the map. If the key does not exist in the map,
	 * null is returned.
	 * 
	 * @param name the key of the wrapper
	 * @return the {@link BooleanProperty} wrapper represented by a given key in the map
	 */
	public static BooleanProperty getBoolean(String name){
		return booleanMap.get(name);
	}
	/**
	 * Gets whether the key exists in the map.
	 * 
	 * @param name the key
	 * @return True if the key exists in the map, false otherwise.
	 */
	public static boolean hasBoolean(String name){
		return getBoolean(name) != null;
	}
	/**
	 * Gets a boolean value from the wrapper represented by a given key. If the key does not exist, false is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @return boolean value saved in the wrapper, false if the key does not exist.
	 */
	public static boolean getBooleanValue(String name){
		return getBooleanValue(name, false);
	}
	/**
	 * Gets a boolean value from the wrapper represented by a given key. If the key does not exist, an initial value is returned.
	 * 
	 * @param name the key representing the wrapper
	 * @param iniVal a value to return if the key does not exist
	 * @return boolean value saved in the wrapper, an initial value if the key does not exist.
	 */
	public static boolean getBooleanValue(String name, boolean iniVal){
		BooleanProperty source = booleanMap.get(name);
		if(source == null)
			return iniVal;
		return source.get();
	}
	
	//--------------------------------------------------------------------
	//---------------------------IOs--------------------------------------
	//--------------------------------------------------------------------
	
	private static void parseXml(File file) throws SAXException, IOException, ParserConfigurationException{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(file);
		
		doc.getDocumentElement().normalize();
		NodeList constantList = doc.getElementsByTagName("constant");
		for (int i = 0; i < constantList.getLength(); i++) {
			Node node = constantList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				String value = element.getTextContent();
				if(value == null)
					continue;
				
				String namestr = element.getAttribute("name");
				Objects.requireNonNull(namestr, "Name attribute must exist");
				
				String typestr = element.getAttribute("type");
				if(typestr == null)
					typestr = "string";
				
				if(typestr.equalsIgnoreCase("number") || typestr.equalsIgnoreCase("double")){
					double ini = FlashUtil.toDouble(value);
					putNumber(namestr, ini);
				}else if(typestr.equalsIgnoreCase("boolean") || typestr.equalsIgnoreCase("bool")){
					boolean ini = FlashUtil.toBoolean(value);
					putBoolean(namestr, ini);
				}else{
					putString(namestr, value);
				}
			}
		}
	}
	private static void saveXml(File file){
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add("<?xml version=\"1.0\" ?>");
		lines.add("<constants-handler>");
		fillXmlBoolean(lines);
		fillXmlDouble(lines);
		fillXmlString(lines);
		lines.add("</constants-handler>");
		
		try {
			Files.write(file.toPath(), lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private static void fillXmlString(ArrayList<String> lines){
		String[] names = getStringMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"string\">"+(getStringValue(names[i]))+"</constant>");
	}
	private static void fillXmlBoolean(ArrayList<String> lines){
		String[] names = getBooleanMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"boolean\">"+(getBooleanValue(names[i]))+"</constant>");
	}
	private static void fillXmlDouble(ArrayList<String> lines){
		String[] names = getNumberMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"double\">"+(getNumberValue(names[i]))+"</constant>");
	}
	
	/**
	 * Loads constant values from an XML file using the XML DOM parser.
	 * <p>
	 * The syntax of the XML values is as follows:<br>
	 * {@code<constants-handler>}<br>
	 * 		{@code<constant name="boolean constant" type="boolean">true</constant>}<br>
	 * 		{@code<constant name="string constant" type="string">a string</constant>}<br>
	 * 		{@code<constant name="number constant" type="double">15.5</constant>}<br>
	 * {@code</constants-handler>}
	 * </p>
	 * 
	 * @param file the path of the xml file
	 * @throws IllegalArgumentException if the given path is missing or is not a file
	 */
	public static void loadConstantsFromXml(String file){
		File wfile = new File(file);
		if(!wfile.exists())
			throw new IllegalArgumentException("File is missing");
		if(!wfile.isFile())
			throw new IllegalArgumentException("Given path is not a file");
		
		try {
			parseXml(wfile);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	/**
	 * Saves values from the maps to an XML file.
	 * <p>
	 * The syntax of the XML values is as follows:<br>
	 * {@code<constants-handler>}<br>
	 * 		{@code<constant name="boolean constant" type="boolean">true</constant>}<br>
	 * 		{@code<constant name="string constant" type="string">a string</constant>}<br>
	 * 		{@code<constant name="number constant" type="double">15.5</constant>}<br>
	 * {@code</constants-handler>}
	 * </p>
	 * 
	 * @param file the path of the xml file
	 */
	public static void saveConstantsToXml(String file){
		File wfile = FileStream.getFile(file);
		if(!wfile.isFile())
			throw new IllegalArgumentException("Given path is not a file");
		saveXml(wfile);
	}
	
	/**
	 * Prints all values in the maps to a {@link Log}.
	 * @param log the log to print to
	 */
	public static void printAll(Log log){
		log.log("\n" + getBooleanPrint() + "\n" + getNumberPrint() + "\n" + getStringPrint(),
				"ConstantsHandler");
	}
	/**
	 * Gets a string representing all string values in the string map. The string should be used for printing.
	 * @return a string representing string values
	 */
	public static String getStringPrint(){
		String[] names = getStringMapNames();
		
		String str = "String Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getStringValue(names[i])+"\n";
		return str;
	}
	/**
	 * Gets a string representing all boolean values in the boolean map. The string should be used for printing.
	 * @return a string representing boolean values
	 */
	public static String getBooleanPrint(){
		String[] names = getBooleanMapNames();
		
		String str = "Boolean Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getBooleanValue(names[i])+"\n";
		return str;
	}
	/**
	 * Gets a string representing all number values in the number map. The string should be used for printing.
	 * @return a string representing number values
	 */
	public static String getNumberPrint(){
		String[] names = getNumberMapNames();
		
		String str = "Number Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getNumberValue(names[i])+"\n";
		return str;
	}
}
