package edu.flash3388.flashlib.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
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
		
		this.in = in;
		
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
	
	private Object parseArray(Element element) 
			throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException, NoSuchFieldException, SecurityException, XMLTypeException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException {
		String className = element.getAttribute(XMLTagData.CLASS_ATTRIBUTE);
		Class<?> classObject = getClass(className);
		
		String lengthStr = element.getAttribute(XMLTagData.LENGTH_ATTRIBUTE);
		int length;
		try {
			length = Integer.parseInt(lengthStr);
		} catch(NumberFormatException e) {
			throw new IOException(e);
		}
		
		NodeList children = element.getChildNodes();
		Object[] array = (Object[]) Array.newInstance(classObject, length);
		
		int index = 0;
		
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			
			if (node.getNodeType() != Node.ELEMENT_NODE) 
				continue;
			
			Element childElement = (Element)node;
			Object entry = parseElement(childElement);
			
			array[index++] = entry;
		}
		
		if (index < length) 
			throw new IOException("Missing array elements");
		
		if (isPrimitiveClassName(className))
			return arrayToPrimitiveArray(array, classObject);
		
		return array;
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
				case CHAR:
					return element.getTextContent().charAt(0);
				case BOOLEAN:
					return Boolean.parseBoolean(element.getTextContent());
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
				case ARRAY:
					return parseArray(element);
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
	
	private Class<?> getClass(String className) throws ClassNotFoundException {
		if (className.equalsIgnoreCase("byte"))
			return Byte.class;
		if (className.equalsIgnoreCase("short"))
			return Short.class;
		if (className.equalsIgnoreCase("int"))
			return Integer.class;
		if (className.equalsIgnoreCase("long"))
			return Long.class;
		if (className.equalsIgnoreCase("float"))
			return Float.class;
		if (className.equalsIgnoreCase("double"))
			return Double.class;
		if (className.equalsIgnoreCase("boolean"))
			return Boolean.class;
		if (className.equalsIgnoreCase("char"))
			return Character.class;
		
		return Class.forName(className);
	}
	
	private boolean isPrimitiveClassName(String className) {
		if (className.equalsIgnoreCase("byte"))
			return true;
		if (className.equalsIgnoreCase("short"))
			return true;
		if (className.equalsIgnoreCase("int"))
			return true;
		if (className.equalsIgnoreCase("long"))
			return true;
		if (className.equalsIgnoreCase("float"))
			return true;
		if (className.equalsIgnoreCase("double"))
			return true;
		if (className.equalsIgnoreCase("boolean"))
			return true;
		if (className.equalsIgnoreCase("char"))
			return true;
		
		return false;
	}
	
	private Object arrayToPrimitiveArray(Object[] array, Class<?> type) {
		if (type.equals(Byte.class)) {
			byte[] parray = new byte[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (byte) array[i];
			
			return parray;
		}
		if (type.equals(Short.class)) {
			short[] parray = new short[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (short) array[i];
			
			return parray;
		}
		if (type.equals(Integer.class)) {
			int[] parray = new int[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (int) array[i];
			
			return parray;
		}
		if (type.equals(Long.class)) {
			long[] parray = new long[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (long) array[i];
			
			return parray;
		}
		if (type.equals(Float.class)) {
			float[] parray = new float[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (float) array[i];
			
			return parray;
		}
		if (type.equals(Double.class)) {
			double[] parray = new double[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (double) array[i];
			
			return parray;
		}
		if (type.equals(Boolean.class)) {
			boolean[] parray = new boolean[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (boolean) array[i];
			
			return parray;
		}
		if (type.equals(Character.class)) {
			char[] parray = new char[array.length];
			for (int i = 0; i < array.length; i++)
				parray[i] = (char) array[i];
			
			return parray;
		}
		
		return null;
	}
}