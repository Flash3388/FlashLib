package edu.flash3388.flashlib.communications;

@FunctionalInterface
public interface DataListener {
	void newData(byte[] data);
}
