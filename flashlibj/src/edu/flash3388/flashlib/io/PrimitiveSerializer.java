package edu.flash3388.flashlib.io;

public interface PrimitiveSerializer {

	byte[] toBytes(short value);
	byte[] toBytes(int value);
	byte[] toBytes(long value);
	byte[] toBytes(float value);
	byte[] toBytes(double value);
	
	short toShort(byte[] bytes);
	int toInt(byte[] bytes);
	long toLong(byte[] bytes);
	float toFloat(byte[] bytes);
	double toDouble(byte[] bytes);
}
