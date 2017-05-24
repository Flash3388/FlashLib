package edu.flash3388.flashlib.vision;

public interface FilterCreator {
	ProcessingFilter create(int id);
	byte getSaveId(ProcessingFilter filter);
}
