package edu.flash3388.flashlib.io;

import edu.flash3388.flashlib.io.XMLTagData;
import edu.flash3388.flashlib.io.XMLTagData.Type;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

public class XMLObjectOutputStream extends ObjectOutputStream {

	private static class XMLDataOutputStream {
		
		private Writer out;
		
		XMLDataOutputStream(OutputStream out) {
			this.out = new OutputStreamWriter(out);
		}
		
		String getIndentationString(int level) {
			StringBuilder builder = new StringBuilder();
			
			while (level > 0) {
				builder.append('\t');
				level--;
			}
			
			return builder.toString();
		}
		
		String getAttributesString(String name, String className) {
			StringBuilder builder = new StringBuilder();
			
			if (name != null) {
				builder.append(String.format("%s=\"%s\"", XMLTagData.NAME_ATTRIBUTE, name));
			}
			if (className != null) {
				if (builder.length() > 0)
					builder.append(' ');
				builder.append(String.format("%s=\"%s\"", XMLTagData.CLASS_ATTRIBUTE, className));
			}
			
			return builder.toString();
		}
		
		
		void writeTag(String tag, String value, String name, String className, int indentationLevel) throws IOException {
			String attributes = getAttributesString(name, className);
			if (!attributes.isEmpty()) {
				out.write(String.format("%s<%s %s>%s</%s>\n", 
						getIndentationString(indentationLevel),
						tag, attributes, value, tag));
			} else {
				out.write(String.format("%s<%s>%s</%s>\n", 
						getIndentationString(indentationLevel),
						tag, value, tag));
			}
		}
		void writeTagHeader(String tag, String name, String className, int indentationLevel) throws IOException {
			String attributes = getAttributesString(name, className);
			if (!attributes.isEmpty()) {
				out.write(String.format("%s<%s %s>\n", 
						getIndentationString(indentationLevel),
						tag, attributes));
			} else {
				out.write(String.format("%s<%s>\n", 
						getIndentationString(indentationLevel),
						tag));
			}
		}
		void writeTagFooter(String tag, int indentationLevel) throws IOException {
			out.write(String.format("%s</%s>\n", 
					getIndentationString(indentationLevel),
					tag));
		}
		
		public void flush() throws IOException {
			out.flush();
		}
		
		public void close() throws IOException {
			out.close();
		}
	}
	
	private XMLDataOutputStream out;
	
	
	public XMLObjectOutputStream(OutputStream out) throws IOException {
		this.out = new XMLDataOutputStream(out);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}
	
	@Override
	protected void writeObjectOverride(Object obj) throws IOException {
		try {
			writeObject(obj, null, 0);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IOException(e);
		} 
	}
	
	private void writeValue(Type type, String value, String name, int indentationLevel) throws IOException {
		out.writeTag(type.tag, value, name, null, indentationLevel);
	}
	
	private void writeCollection(Collection<Object> value, String name, int indentationLevel) throws IOException, IllegalArgumentException, IllegalAccessException {
		out.writeTagHeader(Type.COLLECTION.tag, name, value.getClass().getName(), 
				indentationLevel);
		for (Object object : value) {
			writeObject(object, name, indentationLevel + 1);
		}
		out.writeTagFooter(Type.COLLECTION.tag, indentationLevel);
	}
	
	private void writeMap(Map<Object, Object> value, String name, int indentationLevel) throws IOException, IllegalArgumentException, IllegalAccessException {
		out.writeTagHeader(Type.MAP.tag, name, value.getClass().getName(), 
				indentationLevel);
		for (Map.Entry<Object, Object> entry : value.entrySet()) {
			out.writeTagHeader(XMLTagData.TAG_MAP_KEY, name, null, indentationLevel + 1);
			writeObject(entry.getKey(), name, indentationLevel + 2);
			out.writeTagFooter(XMLTagData.TAG_MAP_KEY, indentationLevel + 1);
			
			out.writeTagHeader(XMLTagData.TAG_MAP_VALUE, name, null, indentationLevel + 1);
			writeObject(entry.getValue(), name, indentationLevel + 2);
			out.writeTagFooter(XMLTagData.TAG_MAP_VALUE, indentationLevel + 1);
		}
		out.writeTagFooter(Type.MAP.tag, indentationLevel);
	}
	
	private void writeEnumValue(Object object, String name, int indentationLevel) throws IOException {
		out.writeTag(Type.ENUM.tag, object.toString(), name,
				object.getClass().getName(), indentationLevel);
	}
	
	@SuppressWarnings("unchecked")
	private void writeObject(Object object, String name, int indentationLevel) throws IOException, IllegalArgumentException, IllegalAccessException {
		if (object == null) 
			return;
		
		Type type = XMLTagData.getObjectType(object);
		
		switch (type) {
			case BYTE:
			case SHORT:
			case INT:
			case LONG:
			case FLOAT:
			case DOUBLE:
			case STRING:
				writeValue(type, object.toString(), name, indentationLevel);
				return;
			case COLLECTION:
				writeCollection((Collection<Object>)object, name, indentationLevel);
				return;
			case MAP:
				writeMap((Map<Object, Object>)object, name, indentationLevel);
				return;
			case ENUM:
				writeEnumValue(object, name, indentationLevel);
				return;
			default:
				break;
		}
		
		out.writeTagHeader(Type.OBJECT.tag, name, 
				object.getClass().getName(), indentationLevel);
		for (Field field : object.getClass().getDeclaredFields()) {
			if (Modifier.isTransient(field.getModifiers()))
				continue;
			
			boolean oldAccess = field.isAccessible();
			
			try {
				if (!field.isAccessible())
					field.setAccessible(true);
				
				Object value = field.get(object);
				writeObject(value, field.getName(), indentationLevel + 1);
			} finally {
				if (field.isAccessible() != oldAccess)
					field.setAccessible(oldAccess);
			}
		}
		out.writeTagFooter(Type.OBJECT.tag, indentationLevel);
	}
}
