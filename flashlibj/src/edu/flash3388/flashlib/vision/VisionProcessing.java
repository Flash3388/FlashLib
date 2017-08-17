package edu.flash3388.flashlib.vision;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

/**
 * VisionProcessing is the basis for the dynamic vision system. It provides you with the ability to load different
 * vision filters that will be used when running the vision runner. It is possible to use different libraries for vision.
 * The library needs to have a class which implements {@link VisionSource}.
 * <p>
 * Vision processing is done by using filters to filter out data from images which are stored in a vision source. All filters
 * are used one by one. At the end enough data should be filtered out for the vision source to identify only specific contours
 * in the image which are the objects we are looking for.
 * </p>
 * <p>
 * By default, analysis data is returned from {@link VisionSource#getResult()} which provides simple data.
 * But it is possible to use an {@link AnalysisCreator} to provide more data from vision.
 * </p>
 * <p>
 * Data can be loaded from and saved to XML files or bytes, for transfer of processing data.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * 
 * @see VisionFilter
 * @see VisionSource
 * @see AnalysisCreator
 */
public final class VisionProcessing {
	
	private List<VisionFilter> filters;
	private AnalysisCreator analysisCreator;
	
	private String name;
	private static byte instances = 0;
	
	/**
	 * Creates a new vision processing objects. 
	 * @param name the name of the processing
	 */
	public VisionProcessing(String name){
		this.name = name;
		filters = new ArrayList<VisionFilter>();
	}
	/**
	 * Creates a new vision processing objects. 
	 */
	public VisionProcessing(){
		this("processing"+(++instances));
	}
	
	/**
	 * Gets the name of the processing
	 * @return the name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Sets the {@link AnalysisCreator} object used by this processing to create the appropriate
	 * {@link Analysis} from the vision. If the creator is null, {@link VisionSource#getResult()} will
	 * be used to get the {@link Analysis}.
	 * @param creator the creator object
	 */
	public void setAnalysisCreator(AnalysisCreator creator){
		this.analysisCreator = creator;
	}
	/**
	 * Gets the {@link AnalysisCreator} object used by this processing to create the appropriate
	 * {@link Analysis} from the vision. If the creator is null, {@link VisionSource#getResult()} will
	 * be used to get the {@link Analysis}.
	 * @return the creator object, or null if it not set
	 */
	public AnalysisCreator getAnalysisCreator(){
		return analysisCreator;
	}
	
	/**
	 * Adds a new filter to the filter list. When processing an image with this processing, this filter will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filter the filter to be used
	 */
	public void addFilter(VisionFilter filter){
		filters.add(filter);
	}
	/**
	 * Adds new filters to the filter list. When processing an image with this processing, those filters will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filters the filter to be used
	 */
	public void addFilters(VisionFilter... filters){
		for (VisionFilter filter : filters)
			addFilter(filter);
	}
	/**
	 * Removes a filter from the filter list. The removed filter will no longer be used when analyzing images.
	 * @param filter filter to remove
	 */
	public void removeFilter(VisionFilter filter){
		filters.remove(filter);
	}
	/**
	 * Removes a filter from the given index. The removed filter will no longer be used when analyzing images. If no filter
	 * exists at that index, nothing will happen.
	 * @param idx index of filter to remove
	 */
	public void removeFilter(int idx){
		if(idx < 0 || idx >= filters.size())
			return;
		filters.remove(idx);
	}
	/**
	 * Gets a filter at the given index. If no filter exists at that index, null will be returned.
	 * 
	 * @param index the index 
	 * @return a filter at that index, or null if none exist.
	 */
	public VisionFilter getFilter(int index){
		if(index < 0 || index >= filters.size())
			return null;
		return filters.get(index);
	}
	/**
	 * Gets all the filters as an array.
	 * @return all the filters
	 */
	public VisionFilter[] getFilters(){
		return filters.toArray(new VisionFilter[filters.size()]);
	}
	
	/**
	 * Processes all filters using a given vision source. Calls {@link VisionFilter#process(VisionSource)}
	 * for all the filters.
	 * @param source the vision source for analyzing.
	 */
	public void process(VisionSource source){
		for (VisionFilter filter : filters)
			filter.process(source);
	}
	/**
	 * Processes all filters using a given vision source and returns an {@link Analysis} object which contains the
	 * result. The result is received from the {@link AnalysisCreator} object set to this processing, or if no creator
	 * is set, {@link VisionSource#getResult()} is used. 
	 * Calls {@link VisionFilter#process(VisionSource)} for all the filters.
	 * @param source the vision source for analyzing.
	 * @return the result of the processing
	 */
	public Analysis processAndGet(VisionSource source){
		process(source);
		return analysisCreator != null? analysisCreator.createAnalysis(source, source.getContours()) : 
			source.getResult();
	}
	/**
	 * Processes all filters using a given vision source and returns the result of {@link VisionSource#getResults()}. 
	 * Calls {@link VisionFilter#process(VisionSource)} for all the filters.
	 * @param source the vision source for analyzing.
	 * @return the result of the processing
	 */
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
		if(!VisionFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		name = filters[0];
		for (int i = 1; i < filters.length - 1; i++) {
			String[] splits = filters[i].split(":");
			if(splits.length < 1) continue;
			String namestr = splits[0];
			
			Map<String, VisionParam> params = new HashMap<String, VisionParam>();
			
			for (int j = 1; j < splits.length; j++) {
				String[] tSpl = splits[j].split(",");
				if(tSpl.length != 3)
					continue;
				
				try {
					VisionParam p = VisionParam.createParam(tSpl[0], tSpl[1], tSpl[2]);
					if(p != null)
						params.put(tSpl[0], p);
				} catch (RuntimeException e) {
					continue;
				}
			}
			
			addFilter(VisionFilter.createFilter(namestr, params));
		}
		String creatorData = filters[filters.length-1];
		if(!creatorData.isEmpty()){
			String[] splits = creatorData.split(":");
			if(splits.length < 1) return;
			String namestr = splits[0];
			
			Map<String, VisionParam> params = new HashMap<String, VisionParam>();
			
			for (int j = 1; j < splits.length; j++) {
				String[] tSpl = splits[j].split(",");
				if(tSpl.length != 3)
					continue;
				
				try {
					VisionParam p = VisionParam.createParam(tSpl[0], tSpl[1], tSpl[2]);
					if(p != null)
						params.put(tSpl[0], p);
				} catch (RuntimeException e) {
					continue;
				}
			}
			setAnalysisCreator(AnalysisCreator.create(namestr, params));
		}
	}
	private void parseXml(String file) throws SAXException, IOException, ParserConfigurationException{
		if(!VisionFilter.hasFilterCreator())
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
				Map<String, VisionParam> params = new HashMap<String, VisionParam>();
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
					
					VisionParam p = VisionParam.createParam(name, type, val);
					if(p == null)
						throw new RuntimeException("Invalid type attribute for param "+name+": "+type);
					params.put(name, p);
				}
				
				addFilter(VisionFilter.createFilter(namestr, params));
			}
		}
		NodeList creatorList = doc.getElementsByTagName("analysis-creator");
		if(creatorList.getLength() > 0){
			Element element = (Element) creatorList.item(0);
			String namestr = element.getAttribute("name");
			
			NodeList paramsNodeList = element.getElementsByTagName("param");
			Map<String, VisionParam> params = new HashMap<String, VisionParam>();
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
				
				VisionParam p = VisionParam.createParam(name, type, val);
				if(p == null)
					throw new RuntimeException("Invalid type attribute for param "+name+": "+type);
				params.put(name, p);
			}
			
			setAnalysisCreator(AnalysisCreator.create(namestr, params));
		}
	}
	
	/**
	 * Gets a byte array containing all the filter data. Can be used to transmit this processing object for use by 
	 * a remote source.
	 * @return a byte array data of this object
	 * @throws IllegalStateException if no filter creator exists ({@link VisionFilter#hasFilterCreator()} returns false).
	 */
	public byte[] toBytes(){
		if(!VisionFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		VisionFilter[] filters = getFilters();
		String filterstr = name+"|";
		for (int i = 0; i < filters.length; i++) {
			String name = VisionFilter.getSaveName(filters[i]);
			filterstr += name + ":";
					
			VisionParam[] params = VisionParam.getParameters(filters[i]);
			for (int j = 0; j < params.length; j++) 
				filterstr += params[j].getName() + "," + params[j].getType() + "," + params[j].getValue() + 
				(j < params.length - 1? ":" : "");
			filterstr +=  "|";
		}
		if(analysisCreator != null){
			filterstr += analysisCreator.getClass().getName();
			VisionParam[] params = VisionParam.getParameters(analysisCreator);
			for (int j = 0; j < params.length; j++) 
				filterstr += params[j].getName() + "," + params[j].getType() + "," + params[j].getValue() + 
				(j < params.length - 1? ":" : "");
		}
		return filterstr.getBytes();
	}
	/**
	 * Saves all the filters from this object to an XML file for reuse.
	 * @param file path to the file for use.
	 * @throws IllegalStateException if no filter creator exists ({@link VisionFilter#hasFilterCreator()} returns false).
	 */
	public void saveXml(String file){
		if(!VisionFilter.hasFilterCreator())
			throw new IllegalStateException("Missing filter creator");
		
		VisionFilter[] filters = getFilters();
		ArrayList<String> lines = new ArrayList<String>();
		
		lines.add("<?xml version=\"1.0\" ?>");
		lines.add("<vision name=\""+name+"\">");
		for (VisionFilter filter : filters) {
			lines.add("\t<filter name=\""+VisionFilter.getSaveName(filter)+"\">");
			VisionParam[] params = VisionParam.getParameters(filter);
			if(params != null){
				for (VisionParam d : params) 
					lines.add("\t\t<param name=\""+d.getName()+"\" type=\""+d.getType()+"\">"+
								d.getValue()+"</param>");
			}
			lines.add("\t</filter>");
		}
		if(analysisCreator != null){
			lines.add("\t<analysis-creator name=\""+analysisCreator.getClass().getName()+"\">");
			VisionParam[] params = VisionParam.getParameters(analysisCreator);
			if(params != null){
				for (VisionParam d : params) 
					lines.add("\t\t<param name=\""+d.getName()+"\" type=\""+d.getType()+"\">"+
								d.getValue()+"</param>");
			}
			lines.add("\t</analysis-creator>");
		}
		lines.add("</vision>");
		
		try {
			Files.write(Paths.get(file), lines, StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Creates a new processing object from a byte array containing all the filter data. 
	 * @param bytes a byte array containing all the filter data
	 * @return a new processing object
	 * @throws IllegalStateException if no filter creator exists ({@link VisionFilter#hasFilterCreator()} returns false).
	 */
	public static VisionProcessing createFromBytes(byte[] bytes){
		VisionProcessing proc = new VisionProcessing();
		proc.loadFilters(bytes);
		return proc;
	}
	/**
	 * Creates a new processing object from an XML file.
	 * @param file the xml file
	 * @return a new processing object, or null if parsing failed
	 * @throws RuntimeException if an error has occured while parsing
	 * @throws IllegalStateException if no filter creator exists ({@link VisionFilter#hasFilterCreator()} returns false).
	 */
	public static VisionProcessing createFromXml(String file){
		if(!new File(file).isFile())
			return null;
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new RuntimeException(e);
		}
		return proc;
	}
}
