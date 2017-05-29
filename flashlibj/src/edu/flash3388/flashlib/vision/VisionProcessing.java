package edu.flash3388.flashlib.vision;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.flash3388.flashlib.io.FileStream;
import edu.flash3388.flashlib.util.FlashUtil;

public class VisionProcessing {
	
	private List<ProcessingFilter> filters;
	
	public VisionProcessing(){
		filters = new ArrayList<ProcessingFilter>();
	}
	
	public void addFilter(ProcessingFilter filter){
		filters.add(filter);
	}
	public void addFilters(ProcessingFilter... filters){
		for (ProcessingFilter filter : filters)
			addFilter(filter);
	}
	public void removeFilter(ProcessingFilter filter){
		filters.remove(filter);
	}
	public void removeFilter(int idx){
		filters.remove(idx);
	}
	public ProcessingFilter getFilter(int index){
		return filters.get(index);
	}
	public ProcessingFilter[] getFilters(){
		return filters.toArray(new ProcessingFilter[0]);
	}
	
	public void process(VisionSource source){
		for (ProcessingFilter filter : filters)
			filter.process(source);
	}
	public Analysis processAndGet(VisionSource source){
		process(source);
		return source.getResult();
	}
	public Analysis[] processAndGetAll(VisionSource source){
		process(source);
		return source.getResults();
	}
	
	public void loadFilters(byte[] bytes){
		String filterstr = new String(bytes);
		String[] filters = filterstr.split("|");
		loadFilters(filters);
	}
	public void loadFilters(String file) throws NullPointerException, IOException{
		String[] filters = FileStream.readAllLines(file);
		loadFilters(filters);
	}
	public void loadFilters(String[] filters){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		for (int i = 0; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			int id = FlashUtil.toInt(splits[0]);
			double[] param;
			
			if(splits.length < 2)
				param = new double[0];
			else
				param = FlashUtil.toDoubleArray(Arrays.copyOfRange(splits, 1, splits.length));
			
			addFilter(ProcessingFilter.createFilter(id, param));
		}
	}
	public void parseXml(String file) throws SAXException, IOException, ParserConfigurationException, XMLParseException{
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		File infile = new File(file);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(infile);
		
		doc.getDocumentElement().normalize();
		NodeList filterList = doc.getElementsByTagName("filter");
		for (int i = 0; i < filterList.getLength(); i++) {
			Node node = filterList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				String idstr = element.getAttribute("id");
				int id = -1;
				try{
					id = Integer.parseInt(idstr);
				}catch(NumberFormatException e){
					throw new XMLParseException("Cannot parse id attribute for filter: FormatException");
				}
				if(id < 0)
					throw new XMLParseException("Id attribute for filter has illegal value: "+id);
				
				NodeList paramsNodeList = element.getElementsByTagName("param");
				double[] params = new double[paramsNodeList.getLength()];
				for (int j = 0; j < params.length; j++) {
					String str = paramsNodeList.item(j).getTextContent();
					try{
						params[j] = Double.parseDouble(str);
					}catch(NumberFormatException e){
						throw new XMLParseException("Cannot parse param attribute for filter: FormatException");
					}
				}
				
				addFilter(ProcessingFilter.createFilter(id, params));
			}
		}
	}
	
	public byte[] toBytes(){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		ProcessingFilter[] filters = getFilters();
		String filterstr = "";
		for (int i = 0; i < filters.length; i++) {
			int id = ProcessingFilter.getSaveId(filters[i]);
			String params = FlashUtil.toDataString(FlashUtil.toStringArray(filters[i].getParameters()), ":");
			filterstr += id + ":" + params + "|";
		}
		return filterstr.substring(0, filterstr.length()).getBytes();
	}
	public void saveToFile(String file){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		ProcessingFilter[] filters = getFilters();
		String[] filterstr = new String[filters.length];
		for (int i = 0; i < filters.length; i++) {
			int id = ProcessingFilter.getSaveId(filters[i]);
			String params = FlashUtil.toDataString(FlashUtil.toStringArray(filters[i].getParameters()), ":");
			filterstr[i] = id + ":" + params;
		}
		FileStream.writeLines(file, filterstr);
	}
	public void saveXml(String file){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		ProcessingFilter[] filters = getFilters();
		ArrayList<String> lines = new ArrayList<String>();
		
		for (ProcessingFilter filter : filters) {
			lines.add("<filter id=\""+ProcessingFilter.getSaveId(filter)+"\">");
			double[] params = filter.getParameters();
			for (double d : params) 
				lines.add("\t<param>"+d+"</param>");
			lines.add("</filter>");
		}
		FileStream.writeLines(file, lines.toArray(new String[0]));
	}
	
	public static VisionProcessing createFromFile(String file){
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.loadFilters(file);
		} catch (NullPointerException | IOException e) {}
		return proc;
	}
	public static VisionProcessing createFromBytes(byte[] bytes){
		VisionProcessing proc = new VisionProcessing();
		proc.loadFilters(bytes);
		return proc;
	}
	
	public static VisionProcessing createFromXml(String file){
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException | XMLParseException e) {
			return null;
		}
		return proc;
	}
}
