package edu.flash3388.flashlib.io;

import java.util.Collection;
import java.util.Map;

public class XMLTagData {
	private XMLTagData() {}

	static class XMLTypeException extends Exception {
		private static final long serialVersionUID = 1L;
		
		public XMLTypeException(String message) {
			super(message);
		}
	}
	
	static enum Type {
		BYTE("byte", Byte.class), SHORT("short", Short.class), 
		INT("int", Integer.class), LONG("long", Long.class),
		FLOAT("float", Float.class), DOUBLE("double", Double.class), 
		STRING("string", String.class), 
		ARRAY("array", null), COLLECTION("collection", Collection.class), 
		MAP("map", Map.class),
		OBJECT("object", Object.class),
		ENUM("enum", null);
		
		final String tag;
		final Class<?> type;
		
		Type(String tag, Class<?> type) {
			this.tag = tag;
			this.type = type;
		}
	}
	
	static final String TAG_MAP_KEY = "key";
	static final String TAG_MAP_VALUE = "value";
	
	static final String NAME_ATTRIBUTE = "name";
	static final String CLASS_ATTRIBUTE = "class";
	
	static Type getObjectType(Object object) throws XMLTypeException {
		if (object.getClass().isEnum()) 
			return Type.ENUM;
		if (object.getClass().isArray())
			return Type.ARRAY;
		
		for (Type type : Type.values()) {
			if (type.type.isInstance(object)) {
				return type;
			}
		}
		
		throw new XMLTypeException(String.format(
				"Unable to find type for object %s. Should not happen", object.getClass()));
	}
	
	static Type getTagType(String tag) throws XMLTypeException {
		for (Type type : Type.values()) {
			if (type.tag.equals(tag)) {
				return type;
			}
		}
		

		throw new XMLTypeException(String.format(
				"Unable to find type for tag %s. Invalid tag", tag));
	}
}
