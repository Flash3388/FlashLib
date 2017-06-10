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

public abstract class Interpolation {

	private HashMap<Double, Double> map = new HashMap<Double, Double>();
	private double keyMargin;
	private double lastKey = 0;
	
	public Interpolation(double margin){
		this.keyMargin = margin;
	}
	
	public Map<Double, Double> getMap(){
		return map;
	}
	public void setKeyMargin(double keyMargin){
		this.keyMargin = keyMargin;
	}
	public double getKeyMargin(){
		return keyMargin;
	}
	public int getMappedValuesCount(){
		return map.size();
	}
	public void clear(){
		map.clear();
		lastKey = 0;
	}
	
	public double getValue(double key){
		Double val = map.get(key);
		if(val == null) return 0;
		return val;
	}
	public void put(double key, double value){
		map.put(key, value);
		lastKey = key;
	}
	public void putNext(double value){
		put(lastKey + keyMargin, value);
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
	
	public void saveValuesToXml(String file){
		saveXml(file);
	}
	public void loadValuesFromXml(String file){
		try {
			parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public abstract double interpolate(double x);
}
