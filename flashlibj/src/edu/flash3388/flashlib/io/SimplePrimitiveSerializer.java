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
	public short toShort(byte[] bytes) {
		short result = 0;
		
	    for (int i = 0; i < 2; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public int toInt(byte[] bytes) {
		int result = 0;
		
	    for (int i = 0; i < 4; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public long toLong(byte[] bytes) {
		long result = 0;
		
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (bytes[i] & 0xff);
	    }
	    
	    return result;
	}

	@Override
	public float toFloat(byte[] bytes) {
		return Float.intBitsToFloat(toInt(bytes));
	}

	@Override
	public double toDouble(byte[] bytes) {
		return Double.longBitsToDouble(toLong(bytes));
	}

}
