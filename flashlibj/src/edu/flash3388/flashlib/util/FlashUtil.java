package edu.flash3388.flashlib.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * FlashUtil contains utility functions used throughout flashLib.
 * <p>
 * The utilities are divided into types:
 * <ul>
 * 		<li> Time utilities: Provides time stamp data and delay</li>
 * 		<li> Array utilities: Array shifting, copying, enlarging, printing, etc</li>
 * 		<li> Type conversion utilities: Converting between bytes, Strings and other data types </li>
 * 		<li> Parsing utilities: Parsing lines of String for data </li>
 * 		<li> Reflection utilities: Java reflection assistance </li>
 * 		<li> Network utilities: Getting network addresses and interfaces </li>
 * </ul>
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public final class FlashUtil {
	
	/**
	 * The current version of FlashLib.
	 */
	public static final String VERSION = "1.3.0";
	
	private FlashUtil(){}
	
	private static long startTime = 0;
	private static Logger mainLogger;
	private static Throwable loggerInitException = null;
	
	private static final String os = System.getProperty("os.name").toLowerCase();
	private static final String architecture = System.getProperty("os.arch");
	private static final String version = System.getProperty("os.version");
	
	static{
		setStartTime(System.nanoTime());
		try {
			initMainLogger();
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			loggerInitException = e;
		}
	}
	
	//--------------------------------------------------------------------
	//-----------------------General--------------------------------------
	//--------------------------------------------------------------------
	
	private static void setStartTime(long time){
		if(startTime == 0)
			startTime = time;
	}
	
	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of 
	 * milliseconds, subject to the precision and accuracy of system timers and schedulers. The thread does not 
	 * lose ownership of any monitors. This is done by calling {@link Thread#sleep(long)}.
	 * 
	 * @param ms the length of time to sleep in milliseconds.
	 */
	public static void delay(long ms){
		if(ms <= 0) return;
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
		    Thread.currentThread().interrupt();
        }
	}
	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) for the specified number of 
	 * milliseconds, subject to the precision and accuracy of system timers and schedulers. The thread does not 
	 * lose ownership of any monitors. This is done by calling {@link Thread#sleep(long)}.
	 * 
	 * @param secs the length of time to sleep in seconds.
	 */
	public static void delay(double secs){
		delay((long)(secs * 1000));
	}
	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) until a given {@link BooleanSource}
	 * returns true when {@link BooleanSource#get()} is called or a timeout has been reached. While waiting, the current
	 * thread will be placed into sleep for periods of time given as a parameter.
	 * If the timeout is smaller than 1 ms then it is set to 10000 ms. 
	 * 
	 * @param source the boolean source condition
	 * @param timeout delay timeout in milliseconds
	 * @param delayTime the sleep period length in milliseconds
	 * @return true if the given source has returned true, false if the timeout has been reached.
	 */
	public static boolean delayUntil(BooleanSource source, long timeout, long delayTime){
		if(timeout < 0)
			throw new IllegalArgumentException("Timeout cannot be negative");
		if(delayTime < 0)
			throw new IllegalArgumentException("Delay time cannot be negative");
		
		if(timeout < 1)
			timeout = 10000;
		if(delayTime < 1)
			delayTime = 1;
		long start = millis();
		
		while(!source.get() && millis() - start < timeout)
			delay(delayTime);
		return source.get();
	}
	
	/**
	 * Returns the time since the program was started in milliseconds.
	 * 
	 * @return the difference, measured in milliseconds, between the current time and the time when the program was started.
	 */
	public static long millis(){
		return (long) ((System.nanoTime() - startTime) * 1e-6);
	}
	/**
	 * Returns the time since the program was started in milliseconds. The data is returned as an integer and not a long.
	 * 
	 * @return the difference, measured in milliseconds, between the current time and the time when the program was started.
	 */
	public static int millisInt(){
		return (int) millis();
	}
	/**
	 * Returns the time since the program was started in seconds.
	 * 
	 * @return the difference, measured in seconds, between the current time and the time when the program was started.
	 */
	public static double secs(){
		return millis() * 0.001;
	}

	private static void initMainLogger() throws SecurityException, IOException {
		Logger mainLogger = LogUtil.getLogger("flashlib");
		
		FlashUtil.mainLogger = mainLogger;
	}
	
	/**
	 * Returns the main {@link Logger} used throughout the library. If the logger was not created, an exception is thrown.
	 * The logger name "flashlib", and it holds several handlers.
	 * 
	 * @return the main {@link Logger} used throughout flashlib.
	 * 
	 * @throws IllegalStateException if the main logger was not initialized.
	 */
	public static Logger getLogger(){
		if (mainLogger == null)
			throw new IllegalStateException("Flashlib main logger was not initialized", 
					loggerInitException);
		return mainLogger;
	}
    
    //------------index of---------------
    private static void checkIndexes(int length, int start, int end){
    	if(start < 0 || end < 0)
    		throw new IllegalArgumentException("Indexes must be non-negative");
    	if(start > end)
    		throw new IllegalArgumentException("Start index cannot be bigger than end index");
    	if(start >= length || end >= length)
    		throw new ArrayIndexOutOfBoundsException("Indexes must not exceede the array length");
    }
    /**
     * Gets the index of a value in an array between two indexes. 
     * 
     * @param data the array
     * @param start the start index of the search
     * @param end the end index of the search
     * @param ch the value to search
     * @return the index of the value, or -1 if the value was not found
     */
	public static int indexOf(Object[] data, int start, int end, Object ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] != null && data[i].equals(ch)) return i;
		return -1;
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(Object[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @param <T> the type of the array
	 * @return a copy of the given array
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] copy(T[] arr){
		Class<?> type = arr.getClass().getComponentType();
		T[] nArr = (T[]) Array.newInstance(type, arr.length);
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}

	//--------------------------------------------------------------------
	//--------------------------Conversion--------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Converts a double to bytes and fills a given array with those values from the first index of the array. Requires 8 bytes
	 * to place the double in the byte array.
	 * 
	 * @param value value to convert
	 * @param bytes the byte array to place the double in
	 */
	public static void fillByteArray(double value, byte[] bytes){
		fillByteArray(value, 0, bytes);
	}
	/**
	 * Converts a double to bytes and fills a given array with those values from a start index. Requires 8 bytes
	 * to place the double in the byte array.
	 * 
	 * @param value value to convert
	 * @param start the start index of placement
	 * @param bytes the byte array to place the double in
	 */
	public static void fillByteArray(double value, int start, byte[] bytes){
		if(bytes.length  - start < 8) 
			throw new IllegalArgumentException("double requires 8 bytes to be placed in the array");
		
		fillByteArray(Double.doubleToLongBits(value), start, bytes);
	}

	/**
	 * Converts an int to bytes and fills a given array with those values from a start index. Requires 4 bytes
	 * to place the int in the byte array.
	 * 
	 * @param value value to convert
	 * @param bytes the byte array to place the int in
	 */
	public static void fillByteArray(int value, byte[] bytes){
		fillByteArray(value, 0, bytes);
	}
	/**
	 * Converts a int to bytes and fills a given array with those values from the first index of the array. Requires 4 bytes
	 * to place the int in the byte array.
	 * 
	 * @param value value to convert
	 * @param start the start index of placement
	 * @param bytes the byte array to place the int in
	 */
	public static void fillByteArray(int value, int start, byte[] bytes){
		if(bytes.length  - start < 4) 
			throw new IllegalArgumentException("int requires 4 bytes to be placed in the array");
		
		bytes[start + 3] = (byte) (value & 0xff);   
		bytes[start + 2] = (byte) ((value >> 8) & 0xff);   
		bytes[start + 1] = (byte) ((value >> 16) & 0xff);   
		bytes[start]     = (byte) ((value >> 24) & 0xff);
	}
	/**
	 * Converts a long to bytes and fills a given array with those values from a start index. Requires 8 bytes
	 * to place the long in the byte array.
	 * 
	 * @param value value to convert
	 * @param start the start index of placement
	 * @param bytes the byte array to place the long in
	 */
	public static void fillByteArray(long value, int start, byte[] bytes) {
		if(bytes.length  - start < 8) 
			throw new IllegalArgumentException("long requires 8 bytes to be placed in the array");
		
	    for (int i = start + 7; i >= start; i--) {
	    	bytes[i] = (byte)(value & 0xFF);
	        value >>= 8;
	    }
	}
	/**
	 * Converts the bytes from an index of a byte array to a long. Requires 8 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @param s the start index of the long value
	 * @return a long
	 */
	public static long toLong(byte[] b, int s) {
		if(b.length - s < 8) 
			throw new IllegalArgumentException("long requires 8 bytes");
	    long result = 0;
	    int e = s + 8;
	    for (int i = s; i < e; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	
	/**
	 * Converts an int to a byte array 
	 * 
	 * @param value value to convert
	 * @return a byte array containing the int value as bytes
	 */
	public static byte[] toByteArray(int value){
	    byte[] bytes = new byte[4];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	/**
	 * Converts the bytes from an index of a byte array to an int. Requires 4 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @param s the start index of the int value
	 * @return an int
	 */
	public static int toInt(byte[] b, int s){
		if(b.length - s < 4) 
			throw new IllegalArgumentException("int requires 4 bytes");
	    return   b[s + 3] & 0xff |
	            (b[s + 2] & 0xff) << 8 |
	            (b[s + 1] & 0xff) << 16 |
	            (b[s] & 0xff) << 24;
	}
	/**
	 * Converts the bytes from a byte array to a double. Requires 8 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @return a double
	 */
	public static double toDouble(byte[] b) {
	    return toDouble(b, 0);
	}
	/**
	 * Converts the bytes from an index of a byte array to a double. Requires 8 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @param s the start index of the double value
	 * @return a double
	 */
	public static double toDouble(byte[] b, int s) {
		if(b.length - s < 8) 
			throw new IllegalArgumentException("double requires 8 bytes");
		return Double.longBitsToDouble(toLong(b, s));
	}
	
	/**
	 * Converts a string to an int. If the string cannot be converted, 0 is returned.
	 * 
	 * @param s the string to convert
	 * @return an int converted from the given string, or 0 if the string cannot be converted
	 */
	public static int toInt(String s){
		return toInt(s, 0);
	}
	/**
	 * Converts a string to an int. If the string cannot be converted, a default value is returned.
	 * 
	 * @param s the string to convert
	 * @param defaultVal the value to return if the string cannot be converted
	 * @return an int converted from the given string, or a default value if the string cannot be converted
	 */
	public static int toInt(String s, int defaultVal){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	
	/**
	 * Converts a string to a double. If the string cannot be converted, 0 is returned.
	 * 
	 * @param s the string to convert
	 * @return a double converted from the given string, or 0 if the string cannot be converted
	 */
	public static double toDouble(String s){
		return toDouble(s, 0);
	}
	/**
	 * Converts a string to a double. If the string cannot be converted, a default value is returned.
	 * 
	 * @param s the string to convert
	 * @param defaultVal the value to return if the string cannot be converted
	 * @return a double converted from the given string, or a default value if the string cannot be converted
	 */
	public static double toDouble(String s, double defaultVal){
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	
	/**
	 * Converts a string to a boolean. If the string cannot be converted, false is returned.
	 * 
	 * @param s the string to convert
	 * @return a boolean converted from the given string, or false if the string cannot be converted
	 */
	public static boolean toBoolean(String s){
		return toBoolean(s, false);
	}
	/**
	 * Converts a string to a boolean. If the string cannot be converted, a default value is returned.
	 * 
	 * @param s the string to convert
	 * @param defaultVal the value to return if the string cannot be converted
	 * @return a boolean converted from the given string, or a default value if the string cannot be converted
	 */
	public static boolean toBoolean(String s, boolean defaultVal){
		try{
			return Boolean.parseBoolean(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	
	//--------------------------------------------------------------------
	//----------------------Communications--------------------------------
	//--------------------------------------------------------------------
	
    /**
     * Gets the local {@link java.net.InetAddress} of the network interface connected to a give remote address.
     * 
     * @param remote the remote address
     * @return the local {@link java.net.InetAddress} of the network interface
     * @throws SocketException if an I/O error occurs.
     */
	public static InetAddress getLocalAddress(InetAddress remote) throws SocketException{
		byte[] remoteAddrByte = remote.getAddress();
		Enumeration<NetworkInterface> interEnum = NetworkInterface.getNetworkInterfaces();
		
		while(interEnum.hasMoreElements()){
			NetworkInterface inter = interEnum.nextElement();
			List<InterfaceAddress> addresses = inter.getInterfaceAddresses();
			for (int i = 0; i < addresses.size(); i++) {
				InterfaceAddress address = addresses.get(i);
				byte[] byteAddr = address.getAddress().getAddress();
				
				if(byteAddr.length != remoteAddrByte.length)
					continue;
				
				boolean error = false;
				for(int j = 0; j < remoteAddrByte.length - 1; j++){
					if(byteAddr[j] != remoteAddrByte[j]){
						error = true;
						break;
					}
				}
				if(!error)
					return address.getAddress();
			}
		}
		return null;
	}
	
	//--------------------------------------------------------------------
	//------------------------------OS------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets whether or not the current operating system is windows.
	 * @return true if the current OS is windows.
	 */
	public static boolean isWindows(){
		return os.indexOf("win") >= 0;
	}
	/**
	 * Gets whether or not the current operating system is unix or linux.
	 * @return true if the current OS is windows.
	 */
	public static boolean isUnix(){
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0;
	}

	/**
	 * Gets whether or not the current operating system architecture is 64bit.
	 * @return true if the current OS architecture is 64bit.
	 */
	public static boolean isArchitectureX64(){
		return architecture.indexOf("64") >= 0;
	}
}
