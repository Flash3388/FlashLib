package edu.flash3388.flashlib.io;

public interface PrimitiveSerializer {

	byte[] toBytes(short value);
	byte[] toBytes(int value);
	byte[] toBytes(long value);
	byte[] toBytes(float value);
	byte[] toBytes(double value);
	
	short toShort(byte[] bytes, int offset);
	default short toShort(byte[] bytes) {
		return toShort(bytes, 0);
	}

	int toInt(byte[] bytes, int offset);
	default int toInt(byte[] bytes) {
		return toInt(bytes, 0);
	}

	long toLong(byte[] bytes, int offset);
	default long toLong(byte[] bytes) {
		return toLong(bytes, 0);
	}

	float toFloat(byte[] bytes, int offset);
	default float toFloat(byte[] bytes) {
		return toFloat(bytes, 0);
	}

	double toDouble(byte[] bytes, int offset);
	default double toDouble(byte[] bytes) {
		return toDouble(bytes, 0);
	}
}
