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
 * Data can be loaded from and saved to XML files or bytes, for transfer of processing data.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see ProcessingFilter
 * @see VisionSource
 */
public final class VisionProcessing {
	
	private List<ProcessingFilter> filters;
	private String name;
	private static byte instances = 0;
	
	/**
	 * Creates a new vision processing objects. 
	 * @param name the name of the processing
	 */
	public VisionProcessing(String name){
		this.name = name;
		filters = new ArrayList<ProcessingFilter>();
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
	 * Adds a new filter to the filter list. When processing an image with this processing, this filter will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filter the filter to be used
	 */
	public void addFilter(ProcessingFilter filter){
		filters.add(filter);
	}
	/**
	 * Adds new filters to the filter list. When processing an image with this processing, those filters will be used.
	 * The addition works as FIFO, where the first filter added, is the first to be used.
	 * @param filters the filter to be used
	 */
	public void addFilters(ProcessingFilter... filters){
		for (ProcessingFilter filter : filters)
			addFilter(filter);
	}
	/**
	 * Removes a filter from the filter list. The removed filter will no longer be used when analyzing images.
	 * @param filter filter to remove
	 */
	public void removeFilter(ProcessingFilter filter){
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
	public ProcessingFilter getFilter(int index){
		if(index < 0 || index >= filters.size())
			return null;
		return filters.get(index);
	}
	/**
	 * Gets all the filters as an array.
	 * @return all the filters
	 */
	public ProcessingFilter[] getFilters(){
		return filters.toArray(new ProcessingFilter[filters.size()]);
	}
	
	/**
	 * Processes all filters using a given vision source. Calls {@link ProcessingFilter#process(VisionSource)}
	 * for all the filters.
	 * @param source the vision source for analyzing.
	 */
	public void process(VisionSource source){
		for (ProcessingFilter filter : filters)
			filter.process(source);
	}
	/**
	 * Processes all filters using a given vision source and returns the result of {@link VisionSource#getResult()}. 
	 * Calls {@link ProcessingFilter#process(VisionSource)} for all the filters.
	 * @param source the vision source for analyzing.
	 * @return the result of the processing
	 */
	public Analysis processAndGet(VisionSource source){
		process(source);
		return source.getResult();
	}
	/**
	 * Processes all filters using a given vision source and returns the result of {@link VisionSource#getResults()}. 
	 * Calls {@link ProcessingFilter#process(VisionSource)} for all the filters.
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
	
	/**
	 * Gets a byte array containing all the filter data. Can be used to transmit this processing object for use by 
	 * a remote source.
	 * @return a byte array data of this object
	 * @throws IllegalStateException if no filter creator exists ({@link ProcessingFilter#hasFilterCreator()} returns false).
	 */
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
	/**
	 * Saves all the filters from this object to an XML file for reuse.
	 * @param file path to the file for use.
	 * @throws IllegalStateException if no filter creator exists ({@link ProcessingFilter#hasFilterCreator()} returns false).
	 */
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
	

	/**
	 * Creates a new processing object from a byte array containing all the filter data. 
	 * @param bytes a byte array containing all the filter data
	 * @return a new processing object
	 * @throws IllegalStateException if no filter creator exists ({@link ProcessingFilter#hasFilterCreator()} returns false).
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
	 * @throws IllegalStateException if no filter creator exists ({@link ProcessingFilter#hasFilterCreator()} returns false).
	 */
	public static VisionProcessing createFromXml(String file){
		if(!new File(file).isFile())
			return null;
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.parseXml(file);
		} catch (SAXException | IOException | ParserConfigurationException | RuntimeException e) {
			e.printStackTrace();
			return null;
		}
		return proc;
	}
}
