package edu.flash3388.flashlib.io;

import java.util.Collection;
import java.util.Map;

public class XMLTagData {
	private XMLTagData() {}

	static enum Type {
		BYTE("byte", Byte.class), SHORT("short", Short.class), 
		INT("int", Integer.class), LONG("long", Long.class),
		FLOAT("float", Float.class), DOUBLE("double", Double.class), 
		STRING("string", String.class), 
		COLLECTION("collection", Collection.class), MAP("map", Map.class),
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
	
	static Type getValueType(Object object) {
		if (object.getClass().isEnum()) 
			return Type.ENUM;
		
		for (Type type : Type.values()) {
			if (type.type.isInstance(object)) {
				return type;
			}
		}
		
		throw new IllegalArgumentException("Should not happen");
	}
}
