package edu.flash3388.flashlib.vision;

public interface FilterCreator {
	ProcessingFilter create(String name);
	String getSaveName(ProcessingFilter filter);
}
