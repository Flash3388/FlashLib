package edu.flash3388.flashlib.vision;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
		String[] filters = filterstr.split("\\|");
		loadFilters(filters);
	}
	public void loadFilters(String[] filters){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		for (int i = 0; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			int id = FlashUtil.toInt(splits[0]);
			
			Map<String, FilterParam> params = new HashMap<String, FilterParam>();
			
			for (int j = 1; j < splits.length; j++) {
				String[] tSpl = splits[j].split(",");
				if(tSpl.length != 3)
					continue;
				
				try {
					FilterParam p = FilterParam.createParam(tSpl[0], tSpl[1], tSpl[2]);
					if(p != null)
						params.put(tSpl[0], p);
				} catch (RuntimeException e) {
					continue;
				}
			}
			
			addFilter(ProcessingFilter.createFilter(id, params));
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
				Map<String, FilterParam> params = new HashMap<String, FilterParam>();
				for (int j = 0; j < paramsNodeList.getLength(); j++) {
					NamedNodeMap attrs = paramsNodeList.item(j).getAttributes();
					
					String val = paramsNodeList.item(j).getTextContent();
					
					Node n = attrs.getNamedItem("type");
					if(n == null)
						throw new XMLParseException("Type attribute is missing value");
					String type = n.getTextContent();
					
					n = attrs.getNamedItem("name");
					if(n == null)
						throw new XMLParseException("Name attribute is missing value");
					String name = n.getTextContent();
					
					try {
						FilterParam p = FilterParam.createParam(name, type, val);
						if(p == null)
							throw new XMLParseException("Invalid type attribute for param "+name+": "+type);
						params.put(name, p);
					} catch (RuntimeException e) {
						throw new XMLParseException(e.getMessage());
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
			filterstr += id + ":";
					
			FilterParam[] params = filters[i].getParameters();
			for (int j = 0; j < params.length; j++) 
				filterstr += params[j].getName() + "," + params[j].getType() + "," + params[j].getValue() + 
				(j < params.length - 1? ":" : "");
			filterstr +=  "|";
		}
		return filterstr.substring(0, filterstr.length()).getBytes();
	}
	public void saveXml(String file){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		ProcessingFilter[] filters = getFilters();
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add("<vision>");
		for (ProcessingFilter filter : filters) {
			lines.add("\t<filter id=\""+ProcessingFilter.getSaveId(filter)+"\">");
			FilterParam[] params = filter.getParameters();
			for (FilterParam d : params) 
				lines.add("\t\t<param name=\""+d.getName()+"\" type=\""+d.getType()+"\">"+d.getValue()+"</param>");
			lines.add("\t</filter>");
		}
		lines.add("</vision>");
		FileStream.writeLines(file, lines.toArray(new String[0]));
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
