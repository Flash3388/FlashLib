package edu.flash3388.flashlib.math;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.flash3388.flashlib.io.FileStream;

/**
 * Provides a base for interpolation. In the mathematical field of numerical analysis, 
 * interpolation is a method of constructing new data points within the range of a discrete 
 * set of known data points. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class Interpolation {

	private HashMap<Double, Double> map = new HashMap<Double, Double>();
	private double keyMargin = 0;
	
	/**
	 * Creates a base for interpolation with a consistent value margin between 2 x coordinates.
	 * @param margin the margin
	 * @see #setKeyMargin(double)
	 * @see <a href="https://en.wikipedia.org/wiki/Interpolation">https://en.wikipedia.org/wiki/Interpolation</a>
	 */
	public Interpolation(double margin){
		this.keyMargin = margin;
	}
	/**
	 * Creates a base for interpolation.
	 */
	public Interpolation(){}
	
	/**
	 * Gets the value map used to hold the data points.
	 * @return the {@link Map} object
	 */
	public Map<Double, Double> getMap(){
		return map;
	}
	/**
	 * Sets the consistent value margin between 2 x coordinates. This value is used 
	 * in some interpolations.
	 * @param keyMargin the margin
	 */
	public void setKeyMargin(double keyMargin){
		this.keyMargin = keyMargin;
	}
	/**
	 * Gets the consistent value margin between 2 x coordinates. This value is used 
	 * in some interpolations.
	 * @return the margin
	 */
	public double getKeyMargin(){
		return keyMargin;
	}
	/**
	 * Gets the count of values in the map
	 * @return the size of the map
	 */
	public int getMappedValuesCount(){
		return map.size();
	}
	/**
	 * Resets the values in the map
	 */
	public void clear(){
		map.clear();
	}
	
	/**
	 * Gets a value from the map by a given key, which is the x-coordinate.
	 * @param key the x coordinate
	 * @return the y-coordinate matching, or 0 if the key is not in the map
	 */
	public double getValue(double key){
		Double val = map.get(key);
		if(val == null) return 0;
		return val;
	}
	/**
	 * Puts a new value in the map, or edits an existing value from the map.
	 * 
	 * @param key the x-coordinate
	 * @param value the y-coordinate
	 */
	public void put(double key, double value){
		map.put(key, value);
	}
	
	private void saveXml(String file){
		Double[] keys = new Double[map.keySet().size()];
		map.keySet().toArray(keys);
		
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("<interpolation>");
		for (int i = 0; i < keys.length; i++) {
			double x = keys[i];
			double y = map.get(keys[i]);
			
			lines.add("\t<value>");
			lines.add("\t\t<x>"+x+"</x>");
			lines.add("\t\t<y>"+y+"</y>");
			lines.add("\t</value>");
		}
		lines.add("</interpolation>");
		
		FileStream.writeLines(file, lines.toArray(new String[lines.size()]));
	}
	private void parseXml(String file) throws SAXException, IOException, ParserConfigurationException{
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(file);
		
		doc.getDocumentElement().normalize();
		NodeList constantList = doc.getElementsByTagName("value");
		for (int i = 0; i < constantList.getLength(); i++) {
			Node node = constantList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				NodeList xlist = element.getElementsByTagName("x");
				NodeList ylist = element.getElementsByTagName("y");
				
				for (int j = 0; j < xlist.getLength(); j++) {
					Node nodex = xlist.item(j);
					Node nodey = ylist.item(j);
					
					double x = Double.parseDouble(nodex.getTextContent());
					double y = Double.parseDouble(nodey.getTextContent());
					
					put(x, y);
				}
			}
		}
	}
	
	/**
	 * Saves values from the map to an XML file. 
	 * @param file file to save to
	 */
	public void saveValuesToXml(String file){
		saveXml(file);
	}
	/**
	 * Loads values to the map from an XML file.
	 * @param file xml file
	 */
	public void loadValuesFromXml(String file){
		try {
			parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Interpolates a value corresponding to the given x-coordinate using the data points
	 * set in the map.
	 * @param x the x-coordinate
	 * @return an estimated y-coordinate
	 */
	public abstract double interpolate(double x);
}
