package edu.flash3388.flashlib.io;

public class SimplePrimitiveSerializer implements PrimitiveSerializer {

	@Override
	public byte[] toBytes(short value) {
		byte[] bytes = new byte[2];
		
	    for (int i = 1; i >= 0; i--) {
	    	bytes[i] = (byte)(value & 0xff);
	        value >>= 8;
	    }
	    
	    return bytes;
	}

	@Override
	public byte[] toBytes(int value) {
		byte[] bytes = new byte[4];
		
	    for (int i = 3; i >= 0; i--) {
	    	bytes[i] = (byte)(value & 0xff);
	        value >>= 8;
	    }
	    
	    return bytes;
	}

	@Override
	public byte[] toBytes(long value) {
		byte[] bytes = new byte[8];
		
	    for (int i = 7; i >= 0; i--) {
	    	bytes[i] = (byte)(value & 0xff);
	        value >>= 8;
	    }
	    
	    return bytes;
	}

	@Override
	public byte[] toBytes(float value) {
		int intValue = Float.floatToIntBits(value);
		return toBytes(intValue);
	}

	@Override
	public byte[] toBytes(double value) {
		long longValue = Double.doubleToLongBits(value);
		return toBytes(longValue);
	}

	@Override
	public short toShort(byte[] bytes, int offset) {
		short result = 0;

		int end = offset + 2;
	    for (int i = offset; i < end; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public int toInt(byte[] bytes, int offset) {
		int result = 0;

		int end = offset + 4;
	    for (int i = offset; i < end; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public long toLong(byte[] bytes, int offset) {
		long result = 0;

		int end = offset + 8;
	    for (int i = offset; i < end; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public float toFloat(byte[] bytes, int offset) {
		return Float.intBitsToFloat(toInt(bytes, offset));
	}

	@Override
	public double toDouble(byte[] bytes, int offset) {
		return Double.longBitsToDouble(toLong(bytes, offset));
	}

}
