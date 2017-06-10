package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.IOException;
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
import edu.flash3388.flashlib.robot.devices.BooleanDataSource;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.devices.StringDataSource;

public class ConstantsHandler {
	
	private static Map<String, DoubleDataSource.VarDataSource> doubleMap = 
			new HashMap<String, DoubleDataSource.VarDataSource>();
	private static Map<String, StringDataSource.VarDataSource> stringMap = 
			new HashMap<String, StringDataSource.VarDataSource>();
	private static Map<String, BooleanDataSource.VarDataSource> booleanMap = 
			new HashMap<String, BooleanDataSource.VarDataSource>();
	
	//--------------------------------------------------------------------
	//-----------------------General--------------------------------------
	//--------------------------------------------------------------------
	
	public static void clear(){
		doubleMap.clear();
		stringMap.clear();
		booleanMap.clear();
	}
	public static int getTotalMapCount(){
		return getBooleanMapCount() + getNumberMapCount() + getStringMapCount();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Numbers--------------------------------------
	//--------------------------------------------------------------------
	
	public static int getNumberMapCount(){
		return doubleMap.size();
	}
	public static String[] getNumberMapNames(){
		String[] keys = new String[doubleMap.size()];
		return doubleMap.keySet().toArray(keys);
	}
	public static DoubleDataSource removeNumber(String name){
		return doubleMap.remove(name);
	}
	public static DoubleDataSource addNumber(String name, double iniVal){
		DoubleDataSource.VarDataSource source = doubleMap.get(name);
		if(source == null){
			source = new DoubleDataSource.VarDataSource(iniVal);
			doubleMap.put(name, source);
		}
		return source;
	}
	public static DoubleDataSource putNumber(String name, double iniVal){
		DoubleDataSource.VarDataSource source = doubleMap.get(name);
		if(source == null){
			source = new DoubleDataSource.VarDataSource(iniVal);
			doubleMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	public static DoubleDataSource getNumber(String name, double iniVal){
		DoubleDataSource.VarDataSource source = doubleMap.get(name);
		if(source == null){
			source = new DoubleDataSource.VarDataSource(iniVal);
			doubleMap.put(name, source);
		}
		return source;
	}
	public static DoubleDataSource getNumber(String name){
		return doubleMap.get(name);
	}
	public static boolean hasNumber(String name){
		return getNumber(name) != null;
	}
	public static double getNumberNative(String name){
		return getNumberNative(name, 0.0);
	}
	public static double getNumberNative(String name, double iniVal){
		DoubleDataSource source = doubleMap.get(name);
		if(source == null)
			return iniVal;
		return source.get();
	}
	public static int getIntegerNative(String name){
		return getIntegerNative(name, 0);
	}
	public static int getIntegerNative(String name, int iniVal){
		DoubleDataSource source = doubleMap.get(name);
		if(source == null)
			return iniVal;
		return (int) source.get();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Strings--------------------------------------
	//--------------------------------------------------------------------
	
	public static int getStringMapCount(){
		return stringMap.size();
	}
	public static String[] getStringMapNames(){
		String[] keys = new String[stringMap.size()];
		return stringMap.keySet().toArray(keys);
	}
	public static StringDataSource removeString(String name){
		return stringMap.remove(name);
	}
	public static StringDataSource addString(String name, String iniVal){
		StringDataSource.VarDataSource source = stringMap.get(name);
		if(source == null){
			source = new StringDataSource.VarDataSource(iniVal);
			stringMap.put(name, source);
		}
		return source;
	}
	public static StringDataSource putString(String name, String iniVal){
		StringDataSource.VarDataSource source = stringMap.get(name);
		if(source == null){
			source = new StringDataSource.VarDataSource(iniVal);
			stringMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	public static StringDataSource getString(String name, String iniVal){
		StringDataSource.VarDataSource source = stringMap.get(name);
		if(source == null){
			source = new StringDataSource.VarDataSource(iniVal);
			stringMap.put(name, source);
		}
		return source;
	}
	public static StringDataSource getString(String name){
		return stringMap.get(name);
	}
	public static boolean hasString(String name){
		return getString(name) != null;
	}
	public static String getStringNative(String name){
		return getStringNative(name, null);
	}
	public static String getStringNative(String name, String iniVal){
		StringDataSource source = stringMap.get(name);
		if(source == null)
			return iniVal;
		return source.get();
	}
	
	//--------------------------------------------------------------------
	//-----------------------Boolean--------------------------------------
	//--------------------------------------------------------------------
	
	public static int getBooleanMapCount(){
		return booleanMap.size();
	}
	public static String[] getBooleanMapNames(){
		String[] keys = new String[booleanMap.size()];
		return booleanMap.keySet().toArray(keys);
	}
	public static BooleanDataSource removeBoolean(String name){
		return booleanMap.remove(name);
	}
	public static BooleanDataSource addString(String name, boolean iniVal){
		BooleanDataSource.VarDataSource source = booleanMap.get(name);
		if(source == null){
			source = new BooleanDataSource.VarDataSource(iniVal);
			booleanMap.put(name, source);
		}
		return source;
	}
	public static BooleanDataSource putBoolean(String name, boolean iniVal){
		BooleanDataSource.VarDataSource source = booleanMap.get(name);
		if(source == null){
			source = new BooleanDataSource.VarDataSource(iniVal);
			booleanMap.put(name, source);
		}else source.set(iniVal);
		
		return source;
	}
	public static BooleanDataSource getBoolean(String name, boolean iniVal){
		BooleanDataSource.VarDataSource source = booleanMap.get(name);
		if(source == null){
			source = new BooleanDataSource.VarDataSource(iniVal);
			booleanMap.put(name, source);
		}
		return source;
	}
	public static BooleanDataSource getBoolean(String name){
		return booleanMap.get(name);
	}
	public static boolean hasBoolean(String name){
		return getBoolean(name) != null;
	}
	public static boolean getBooleanNative(String name){
		return getBooleanNative(name, false);
	}
	public static boolean getBooleanNative(String name, boolean iniVal){
		BooleanDataSource source = booleanMap.get(name);
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
		
		lines.add("<constants-handler>");
		fillXmlBoolean(lines);
		fillXmlDouble(lines);
		fillXmlString(lines);
		lines.add("</constants-handler>");
		
		String[] linesArr = new String[lines.size()];
		FileStream.writeLines(file.getAbsolutePath(), lines.toArray(linesArr));
	}
	private static void fillXmlString(ArrayList<String> lines){
		String[] names = getStringMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"string\">"+(getStringNative(names[i]))+"</constant>");
	}
	private static void fillXmlBoolean(ArrayList<String> lines){
		String[] names = getBooleanMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"boolean\">"+(getBooleanNative(names[i]))+"</constant>");
	}
	private static void fillXmlDouble(ArrayList<String> lines){
		String[] names = getNumberMapNames();
		
		for (int i = 0; i < names.length; i++) 
			 lines.add("\t<constant name=\""+names[i]+"\" type=\"double\">"+(getNumberNative(names[i]))+"</constant>");
	}
	
	public static void loadConstantsFromXml(String file){
		File wfile = new File(file);
		if(!wfile.exists())
			throw new IllegalArgumentException("File is missing");
		if(!wfile.isFile())
			throw new IllegalArgumentException("Given path is not a file");
		
		try {
			parseXml(wfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void saveConstantsToXml(String file){
		File wfile = FileStream.wrap(file);
		if(!wfile.isFile())
			throw new IllegalArgumentException("Given path is not a file");
		saveXml(wfile);
	}
	
	public static void printAll(Log log){
		log.log("\n" + getBooleanPrint() + "\n" + getNumberPrint() + "\n" + getStringPrint(),
				"ConstantsHandler");
	}
	public static String getStringPrint(){
		String[] names = getStringMapNames();
		
		String str = "String Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getStringNative(names[i])+"\n";
		return str;
	}
	public static String getBooleanPrint(){
		String[] names = getBooleanMapNames();
		
		String str = "Boolean Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getBooleanNative(names[i])+"\n";
		return str;
	}
	public static String getNumberPrint(){
		String[] names = getNumberMapNames();
		
		String str = "Number Constants:\n";
		for (int i = 0; i < names.length; i++) 
			str += "\t"+names[i]+" : "+getNumberNative(names[i])+"\n";
		return str;
	}
}
