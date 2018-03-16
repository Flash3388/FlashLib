package edu.flash3388.flashlib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.flash3388.flashlib.io.XMLTagData.Type;
import edu.flash3388.flashlib.io.XMLTagData.XMLTypeException;

public class XMLObjectInputStream extends ObjectInputStream {

	private Queue<Element> elementsQueue;
	private InputStream in;
	
	public XMLObjectInputStream(InputStream in) throws IOException {
		Document xmlDocument;
		try {
			xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(in);
		} catch (SAXException | ParserConfigurationException e) {
			throw new IOException(e);
		}
		
		elementsQueue = new ArrayDeque<Element>();
		NodeList nodeList = xmlDocument.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
			elementsQueue.add((Element)node);
		}
	}
	
	@Override
	public void close() throws IOException {
		in.close();
	}
	
	@Override
	protected Object readObjectOverride() throws IOException, ClassNotFoundException {
		try {
			return readNextElement();
		} catch (NoSuchFieldException | SecurityException | InstantiationException | 
				IllegalAccessException | XMLTypeException | IllegalArgumentException | 
				InvocationTargetException | NoSuchMethodException e) {
			throw new IOException(e);
		} 
	}
	
	private Object readNextElement() throws IOException, ClassNotFoundException, NoSuchFieldException, SecurityException, InstantiationException, IllegalAccessException, XMLTypeException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Element nextRoot = elementsQueue.poll();
		if (nextRoot == null) {
			throw new IOException("Stream empty of element");
		}
		
		return parseElement(nextRoot);
	}
	
	@SuppressWarnings("unchecked")
	private Object parseCollection(Element element) 
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, XMLTypeException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		String className = element.getAttribute(XMLTagData.CLASS_ATTRIBUTE);
		Class<?> classObject = Class.forName(className);
		
		Object objectInstance = createInstance(classObject);
		
		Collection<Object> collection = (Collection<Object>) objectInstance;
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
			Element childElement = (Element)node;
			Object entry = parseElement(childElement);
			
			collection.add(entry);
		}
		
		return collection;
	}
	@SuppressWarnings("unchecked")
	private Object parseMap(Element element) 
			throws ClassNotFoundException, IOException, IllegalAccessException,
			NoSuchFieldException, SecurityException, InstantiationException, XMLTypeException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		String className = element.getAttribute(XMLTagData.CLASS_ATTRIBUTE);
		Class<?> classObject = Class.forName(className);
		
		Object objectInstance = createInstance(classObject);
		
		Map<Object, Object> map = (Map<Object, Object>) objectInstance;
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
			Element childElement = (Element)node;
			
			Object key = null;
			Object value = null;
			
			NodeList mapDataList = childElement.getChildNodes();
			for (int j = 0; j < mapDataList.getLength(); j++) {
				Node mapDataNode = mapDataList.item(j);
				
				if (mapDataNode.getNodeType() != Node.ELEMENT_NODE) 
					continue;
				
				Element mapDataElement = (Element) mapDataNode;
				String dataName = mapDataElement.getTagName();
				
				Node dataNode = mapDataElement.getFirstChild();
				
				if (dataNode.getNodeType() == Node.ELEMENT_NODE) {
					Element dataElement = (Element)dataNode;
					Object data = parseElement(dataElement);
					
					if (dataName.equalsIgnoreCase(XMLTagData.TAG_MAP_KEY))
						key = data;
					else if (dataName.equalsIgnoreCase(XMLTagData.TAG_MAP_VALUE))
						value = data;
				}
			}
			
			if (key != null && value != null)
				map.put(key, value);
		}
		
		return map;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object parseEnumValue(Element element) throws ClassNotFoundException, IOException {
		String className = element.getAttribute(XMLTagData.CLASS_ATTRIBUTE);
		Class<?> classObject = Class.forName(className);
		
		String value = element.getTextContent();
		
		if (!classObject.isEnum()) {
			throw new IOException("Object is not an enum class: " + className);
		}
		
		Object[] constants = classObject.getEnumConstants();
		for (Object constant : constants) {
			if (constant.equals(value)) {
				return constant;
			}
		}
		
		throw new EnumConstantNotPresentException(
				(Class<? extends Enum>) classObject, value);
	}
	private Object parseElement(Element element) 
			throws ClassNotFoundException, IOException, XMLTypeException, 
			IllegalAccessException, NoSuchFieldException, 
			SecurityException, InstantiationException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		Type type = XMLTagData.getTagType(element.getTagName());
		
		try {
			switch (type) {
				case BYTE:
					return Byte.parseByte(element.getTextContent());
				case SHORT:
					return Short.parseShort(element.getTextContent());
				case INT:
					return Integer.parseInt(element.getTextContent());
				case LONG:
					return Long.parseLong(element.getTextContent());
				case FLOAT:
					return Float.parseFloat(element.getTextContent());
				case DOUBLE:
					return Double.parseDouble(element.getTextContent());
				case STRING:
					return element.getTextContent();
				case COLLECTION:
					return parseCollection(element);
				case MAP:
					return parseMap(element);
				case ENUM:
					return parseEnumValue(element);
				default:
					break;
			}
		} catch(NumberFormatException e) {
			String name = element.getAttribute(XMLTagData.NAME_ATTRIBUTE);
			if (name != null)
				throw new IOException(name, e);
			
			throw new IOException(e);
		}
		
		String className = element.getAttribute(XMLTagData.CLASS_ATTRIBUTE);
		Class<?> classObject = Class.forName(className);
		
		Object objectInstance = createInstance(classObject);
		
		NodeList children = element.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
			Element childElement = (Element)node;
			String name = childElement.getAttribute(XMLTagData.NAME_ATTRIBUTE);
			
			Object value = parseElement(childElement);
			Field field = classObject.getDeclaredField(name);
			
			boolean oldAccess = field.isAccessible();
			
			try {
				if (!field.isAccessible())
					field.setAccessible(true);
				
				field.set(objectInstance, value);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IOException(e);
			} finally {
				if (field.isAccessible() != oldAccess)
					field.setAccessible(oldAccess);
			}
		}
		
		return objectInstance;
	}
	
	private Object createInstance(Class<?> classObject) 
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Constructor<?> constructor = classObject.getConstructor();
		
		boolean oldAccess = constructor.isAccessible();
		
		try {
			if (!constructor.isAccessible())
				constructor.setAccessible(true);
			
			return constructor.newInstance();
		} finally {
			if (constructor.isAccessible() != oldAccess)
				constructor.setAccessible(oldAccess);
		}
	}
}
