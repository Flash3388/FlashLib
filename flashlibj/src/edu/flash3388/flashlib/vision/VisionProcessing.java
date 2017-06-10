package edu.flash3388.flashlib.vision;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.flash3388.flashlib.io.FileStream;

public class VisionProcessing {
	
	private List<ProcessingFilter> filters;
	private String name;
	private static byte instances = 0;
	
	public VisionProcessing(String name){
		this.name = name;
		filters = new ArrayList<ProcessingFilter>();
	}
	public VisionProcessing(){
		this("processing"+(++instances));
	}
	
	public String getName(){
		return name;
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
		return filters.toArray(new ProcessingFilter[filters.size()]);
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
	
	private void loadFilters(byte[] bytes){
		String filterstr = new String(bytes);
		String[] filters = filterstr.split("\\|");
		loadFilters(filters);
	}
	private void loadFilters(String[] filters){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		name = filters[0];
		for (int i = 1; i < filters.length; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			String namestr = splits[0];
			
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
			
			addFilter(ProcessingFilter.createFilter(namestr, params));
		}
	}
	private void parseXml(String file) throws SAXException, IOException, ParserConfigurationException{
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		File infile = new File(file);
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
				.parse(infile);
		
		doc.getDocumentElement().normalize();
		
		NodeList base = doc.getElementsByTagName("vision");
		if(base.getLength() != 1)
			throw new RuntimeException("Missing base tag: vision");
		name = ((Element)base.item(0)).getAttribute("name");
		if(name == null)
			name = FileStream.fileName(file);
		
		NodeList filterList = doc.getElementsByTagName("filter");
		for (int i = 0; i < filterList.getLength(); i++) {
			Node node = filterList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE){
				Element element = (Element) node;
				
				String namestr = element.getAttribute("name");
				
				NodeList paramsNodeList = element.getElementsByTagName("param");
				Map<String, FilterParam> params = new HashMap<String, FilterParam>();
				for (int j = 0; j < paramsNodeList.getLength(); j++) {
					NamedNodeMap attrs = paramsNodeList.item(j).getAttributes();
					
					String val = paramsNodeList.item(j).getTextContent();
					
					Node n = attrs.getNamedItem("type");
					if(n == null)
						throw new RuntimeException("Type attribute is missing value");
					String type = n.getTextContent();
					
					n = attrs.getNamedItem("name");
					if(n == null)
						throw new RuntimeException("Name attribute is missing value");
					String name = n.getTextContent();
					
					try {
						FilterParam p = FilterParam.createParam(name, type, val);
						if(p == null)
							throw new RuntimeException("Invalid type attribute for param "+name+": "+type);
						params.put(name, p);
					} catch (RuntimeException e) {
						throw new RuntimeException(e.getMessage());
					}
				}
				
				addFilter(ProcessingFilter.createFilter(namestr, params));
			}
		}
	}
	
	public byte[] toBytes(){
		if(!ProcessingFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		ProcessingFilter[] filters = getFilters();
		String filterstr = name+"|";
		for (int i = 0; i < filters.length; i++) {
			String name = ProcessingFilter.getSaveName(filters[i]);
			filterstr += name + ":";
					
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
		
		lines.add("<vision name=\""+name+"\">");
		for (ProcessingFilter filter : filters) {
			lines.add("\t<filter name=\""+ProcessingFilter.getSaveName(filter)+"\">");
			FilterParam[] params = filter.getParameters();
			for (FilterParam d : params) 
				lines.add("\t\t<param name=\""+d.getName()+"\" type=\""+d.getType()+"\">"+d.getValue()+"</param>");
			lines.add("\t</filter>");
		}
		lines.add("</vision>");
		FileStream.writeLines(file, lines.toArray(new String[lines.size()]));
	}
	

	public static VisionProcessing createFromBytes(byte[] bytes){
		VisionProcessing proc = new VisionProcessing();
		proc.loadFilters(bytes);
		return proc;
	}
	public static VisionProcessing createFromXml(String file){
		if(!new File(file).isFile())
			return null;
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException | RuntimeException e) {
			return null;
		}
		return proc;
	}
}
