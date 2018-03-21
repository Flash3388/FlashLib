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
	public static final String VERSION = "1.2.1";
	
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
		} catch (InterruptedException e) {}
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
	 * Causes the currently executing thread to sleep (temporarily cease execution) until a given {@link BooleanSource}
	 * returns true when {@link BooleanSource#get()} is called or a timeout has been reached. While waiting, the current
	 * thread will be placed into sleep for periods of time calculated depending on the timeout.
	 * If the timeout is smaller than 1 ms then it is set to 10000 ms. 
	 * 
	 * @param source the boolean source condition
	 * @param timeout delay timeout in milliseconds
	 * @return true if the given source has returned true, false if the timeout has been reached.
	 */
	public static boolean delayUntil(BooleanSource source, long timeout){
		return delayUntil(source, timeout, timeout >> 4);
	}
	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) while a given {@link BooleanSource}
	 * returns true when {@link BooleanSource#get()} is called or until a timeout has been reached. While waiting, the current
	 * thread will be placed into sleep for periods of time given as a parameter.
	 * If the timeout is smaller than 1 ms then it is set to 10000 ms. 
	 * 
	 * @param source the boolean source condition
	 * @param timeout delay timeout in milliseconds
	 * @param delayTime the sleep period length in milliseconds
	 * @return true if the given source has returned true, false if the timeout has been reached.
	 */
	public static boolean delayWhile(BooleanSource source, long timeout, long delayTime){
		if(timeout < 0)
			throw new IllegalArgumentException("Timeout cannot be negative");
		if(delayTime < 0)
			throw new IllegalArgumentException("Delay time cannot be negative");
		
		if(timeout < 1)
			timeout = 10000;
		if(delayTime < 1)
			delayTime = 1;
		long start = millis();
		
		while(source.get() && millis() - start < timeout)
			delay(delayTime);
		return !source.get();
	}
	/**
	 * Causes the currently executing thread to sleep (temporarily cease execution) while a given {@link BooleanSource}
	 * returns true when {@link BooleanSource#get()} is called or until a timeout has been reached. While waiting, the current
	 * thread will be placed into sleep for periods of time calculated depending on the timeout.
	 * If the timeout is smaller than 1 ms then it is set to 10000 ms. 
	 * 
	 * @param source the boolean source condition
	 * @param timeout delay timeout in milliseconds
	 * @return true if the given source has returned true, false if the timeout has been reached.
	 */
	public static boolean delayWhile(BooleanSource source, long timeout){
		return delayUntil(source, timeout, timeout >> 4);
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

	//--------------------------------------------------------------------
	//--------------------------Arrays------------------------------------
	//--------------------------------------------------------------------
	
    //------------is empty---------------
    
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(short[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(int[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(long[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(float[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param arr the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(double[] arr) {
        return arr == null || arr.length == 0; 
    }
	/**
	 * Returns whether or not a given array is empty
	 * 
	 * @param objects the array to check
	 * 
	 * @return True if the array is null or empty
	 */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0; 
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
	public static int indexOf(byte[] data, int start, int end, byte ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(short[] data, int start, int end, short ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(int[] data, int start, int end, int ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(long[] data, int start, int end, long ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(double[] data, int start, int end, double ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(float[] data, int start, int end, float ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	public static int indexOf(char[] data, int start, int end, char ch){
		end = Math.min(data.length - 1, end);
		checkIndexes(data.length, start, end);
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
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
	
	//------------contains---------------
	
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(byte[] data, int start, int end, byte ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(short[] data, int start, int end, short ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(int[] data, int start, int end, int ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(long[] data, int start, int end, long ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(float[] data, int start, int end, float ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(double[] data, int start, int end, double ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(char[] data, int start, int end, char ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	/**
	 * Gets whether an array contains a value between two given indexes.
	 * 
	 * @param data the array
	 * @param start the start index of the search
	 * @param end the end index of the search
	 * @param ch the value to search
	 * @return true if the value is contained in the search area of the array, false otherwise
	 */
	public static boolean arrayContains(Object[] data, int start, int end, Object ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	
	//------------print---------------
	
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(byte[] s){
		for(int i = 0; i < s.length; i++)
			System.out.print((char)s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(short[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(int[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(long[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(double[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(float[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(char[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	/**
	 * Prints the array values.
	 * 
	 * @param s the array to print
	 */
	public static void printArray(Object[] s){
		for(int i = 0; i < s.length; i++)
			System.out.println(s[i]);
	}
	
	//------------shift left---------------
	
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(byte[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(short[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(int[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(long[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(double[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(float[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
	}
	/**
	 * Shifts all array value between the given indexes to the left
	 * 
	 * @param arr the array to shift.
	 * @param start the index where the shift starts
	 * @param end the index where the shift ends
	 */
	public static void shiftArrayL(char[] arr, int start, int end){
		end = Math.min(arr.length - 1, end);
		checkIndexes(arr.length, start-1, end);
		/*for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];*/
		System.arraycopy(arr, start, arr, start-1, end-start);
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
	
	
	//------------resize---------------
	
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static byte[] resize(byte[] arr, int newSize){
		byte[] nArr = new byte[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static short[] resize(short[] arr, int newSize){
		short[] nArr = new short[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static int[] resize(int[] arr, int newSize){
		int[] nArr = new int[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static long[] resize(long[] arr, int newSize){
		long[] nArr = new long[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr; 
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static float[] resize(float[] arr, int newSize){
		float[] nArr = new float[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @return a copy of the given array with the same values and a new size
	 */
	public static double[] resize(double[] arr, int newSize){
		double[] nArr = new double[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr; 
	}
	/**
	 * Creates a copy of an array with a new size. Values from the previous array are saved up to the end of the previous
	 * array or the end of the new array. 
	 * 
	 * @param arr the original array
	 * @param newSize the size of the new array
	 * @param <T> the type of the array
	 * @return a copy of the given array with the same values and a new size
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] resize(T[] arr, int newSize){
		Class<?> type = arr.getClass().getComponentType();
		T[] nArr = (T[]) Array.newInstance(type, newSize);
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	
	//------------copy---------------
	
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static byte[] copy(byte[] arr){
		byte[] nArr = new byte[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static short[] copy(short[] arr){
		short[] nArr = new short[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static int[] copy(int[] arr){
		int[] nArr = new int[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static long[] copy(long[] arr){
		long[] nArr = new long[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr; 
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static float[] copy(float[] arr){
		float[] nArr = new float[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	/**
	 * Creates a copy of a given array with the same values and size as the previous array
	 * 
	 * @param arr the array to copy
	 * @return a copy of the given array
	 */
	public static double[] copy(double[] arr){
		double[] nArr = new double[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr; 
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
	 * Converts a float to bytes and fills a given array with those values from the first index of the array. Requires 4 bytes
	 * to place the float in the byte array.
	 * 
	 * @param value value to convert
	 * @param bytes the byte array to place the float in
	 */
	public static void fillByteArray(float value, byte[] bytes){
		fillByteArray(value, 0, bytes);
	}
	/**
	 * Converts a float to bytes and fills a given array with those values from the first index of the array. Requires 4 bytes
	 * to place the float in the byte array.
	 * 
	 * @param value value to convert
	 * @param start start index for byte placement
	 * @param bytes the byte array to place the float in
	 */
	public static void fillByteArray(float value, int start, byte[] bytes){
		if(bytes.length  - start < 4) 
			throw new IllegalArgumentException("float requires 4 bytes to be placed in the array");
		
		fillByteArray(Float.floatToIntBits(value), start, bytes);
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
	 * Converts a long to bytes and fills a given array with those values from the first index of the array. Requires 8 bytes
	 * to place the long in the byte array.
	 * 
	 * @param value value to convert
	 * @param bytes the byte array to place the long in
	 */
	public static void fillByteArray(long value, byte[] bytes){
		fillByteArray(value, 0, bytes);
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
	 * Converts a long to a byte array 
	 * 
	 * @param value value to convert
	 * @return a byte array containing the long value as bytes
	 */
	public static byte[] toByteArray(long value){
	    byte[] bytes = new byte[8];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	/**
	 * Converts the bytes of a byte array to a long. Requires 8 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @return a long
	 */
	public static long toLong(byte[] b) {
		return toLong(b, 0);
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
	 * Converts the bytes from a byte array to an int. Requires 4 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @return an int
	 */
	public static int toInt(byte[] b){
		return toInt(b, 0);
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
	 * Converts a double to a byte array 
	 * 
	 * @param value value to convert
	 * @return a byte array containing the double value as bytes
	 */
	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    fillByteArray(value, bytes);
	    return bytes;
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
	 * Converts a float to a byte array 
	 * 
	 * @param value value to convert
	 * @return a byte array containing the float value as bytes
	 */
	public static byte[] toByteArray(float value) {
	    byte[] bytes = new byte[4];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	/**
	 * Converts the bytes from a byte array to a float. Requires 4 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @return a float
	 */
	public static float toFloat(byte[] b) {
	    return toFloat(b, 0);
	}
	/**
	 * Converts the bytes from an index of a byte array to a float. Requires 4 bytes for conversion.
	 * 
	 * @param b the byte array
	 * @param s the start index of the float value
	 * @return a float
	 */
	public static float toFloat(byte[] b, int s) {
		if(b.length - s < 4) 
			throw new IllegalArgumentException("float requires 4 bytes");
		return Float.intBitsToFloat(toInt(b, s));
	}
	
	/**
	 * Checks whether 2 byte array are equal in size and value.
	 * 
	 * @param b1 the first array
	 * @param b2 the seconds array
	 * @return true if both arrays are the same size and contain the same values
	 */
	public static boolean equals(byte[] b1, byte[] b2){
		if(b1.length != b2.length) return false;
		for(int i = 0; i < b1.length; i++){
			if(b1[i] != b2[i])
				return false;
		}
		return true;
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
	 * Converts a string to a long. If the string cannot be converted, 0 is returned.
	 * 
	 * @param s the string to convert
	 * @return a long converted from the given string, or 0 if the string cannot be converted
	 */
	public static long toLong(String s){
		return toLong(s, 0);
	}
	/**
	 * Converts a string to a long. If the string cannot be converted, a default value is returned.
	 * 
	 * @param s the string to convert
	 * @param defaultVal the value to return if the string cannot be converted
	 * @return a long converted from the given string, or a default value if the string cannot be converted
	 */
	public static long toLong(String s, long defaultVal){
		try{
			return Long.parseLong(s);
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
	//--------------------------Parsing-----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Merges array values to a single string of values separated by a given separator.
	 * 
	 * @param str the string array 
	 * @param sep the separator
	 * @return a line of merged string values
	 */
	public static String toDataString(String[] str, String sep){
		String s = "";
		for (int i = 0; i < str.length; i++) 
			s += str[i] + sep;
		return s.substring(0, s.length());
	}
	
	/**
	 * Splits a string around a separator and returns the value at a given index
	 * 
	 * @param str the string to split
	 * @param seperator the separator to split the string around
	 * @param index the index of the value to return
	 * @return a value from the split array at the given index
	 */
	public static String splitAndGet(String str, String seperator, int index){
		if(index < 0)
			throw new IllegalArgumentException("Index must be non-negative");
		String[] splits = str.split(seperator);
		if(splits.length < index + 1)
			throw new ArrayIndexOutOfBoundsException("Index is out of splited array bounds");
		return splits[index];
	}
	
	/**
	 * Splits a line around spaces and converts the array of values to a map. Each string in the array is split around a separator and
	 * the 2 values from the split are inserted as key and value into the map.
	 * 
	 * @param line the line to parse
	 * @param separator the separator to split the values around
	 * @return a map of the values in the array.
	 */
	public static Map<String, String> parseValueParameters(String line, String separator){
		String[] split = line.split(" ");
		return parseValueParameters(split, separator);
	}
	
	/**
	 * Converts an array of values to a map. Each string in the array is split around a separator and
	 * the 2 values from the split are inserted as key and value into the map.
	 * 
	 * @param args the array of value to insert into the map
	 * @param separator the separator to split the values around
	 * @return a map of the values in the array.
	 */
	public static Map<String, String> parseValueParameters(String[] args, String separator){
		Map<String, String> map = new HashMap<String, String>();
		for (String param : args) {
			String[] vals = param.split(separator);
			if(vals.length != 2)
				continue;
			map.put(vals[0].trim(), vals[1].trim());
		}
		return map;
	}
	
	/**
	 * Splits a line around a separator, trims the values and returns them in an array.
	 * 
	 * @param line the line to split
	 * @param separator the separator to split the array around
	 * @return an array of the split value from the line
	 */
	public static String[] parseParameters(String line, String separator){
		String[] split = line.trim().split(separator);
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		return split;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Reflection--------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets whether a class inherits from another.
	 * 
	 * @param cl the sub class
	 * @param suCl the super class
	 * @return true if the subclass inherits from the superclass
	 */
	public static boolean isAssignable(Class<?> cl, Class<?> suCl){
		return suCl.isAssignableFrom(cl);
	}
	
	/**
	 * Gets the super class and interfaces inherited and implemented by a type.
	 * 
	 * @param type the {@link java.lang.Class}
	 * @return a {@link java.util.Set} of interfaces and super class extended and inplemented by a type
	 */
    public static Set<Class<?>> getSuperTypes(Class<?> type) {
        Set<Class<?>> supers = new LinkedHashSet<>();
        Class<?> superclass = type.getSuperclass();
        Class<?>[] interfaces = type.getInterfaces();
        
        if (superclass != null && !superclass.equals(Object.class)) 
        	supers.add(superclass);
        if (interfaces != null && interfaces.length > 0) 
        	supers.addAll(Arrays.asList(interfaces));
        return supers;
    }
    /**
     * Gets the generic types defined for a super type of a given class. The method searches
     * all the super types of the given class and finds along the inheritance hierarchy a type which
     * corresponds to the given super type to find. If that type is a generic type, the types used are returned. 
     * Otherwise null is returned.
     * 
     * @param child the subclass 
     * @param supertype the generic super type to find
     * @return an array of the generic types defined, or null if not found.
     */
    public static Type[] findGenericArgumentsOfSuperType(Class<?> child, Class<?> supertype){
		if(!supertype.isAssignableFrom(child))
			return null;
		
		Class<?> interclass = null;
		ParameterizedType paramType = null;
		Type rawType = null;
		
		rawType = child.getGenericSuperclass();
		if(rawType instanceof ParameterizedType){
			paramType = (ParameterizedType)rawType;
			rawType = paramType.getRawType();
		}
		interclass = (Class<?>)rawType;
		
		if(interclass != null){
			if(paramType != null && interclass.equals(supertype))
				return paramType.getActualTypeArguments();
			
			Type[] types = findGenericArgumentsOfSuperType(interclass, supertype);
			if(types != null)
				return types;
		}
		
		Type[] superinterfaces = child.getGenericInterfaces();
		for (int i = 0; i < superinterfaces.length; i++) {
			rawType = superinterfaces[i];
			if(rawType instanceof ParameterizedType){
				paramType = (ParameterizedType)rawType;
				rawType = paramType.getRawType();
			}
			interclass = (Class<?>)rawType;
			
			if(paramType != null && interclass.equals(supertype))
				return paramType.getActualTypeArguments();
			
			Type[] types = findGenericArgumentsOfSuperType(interclass, supertype);
			if(types != null)
				return types;
		}
		return null;
	}
    
    /**
     * Gets whether an object related to a class type.
     * 
     * @param obj the object
     * @param cl a {@link java.lang.Class} object of the class
     * @return true if the object is an instance of the given type, or inherits from it.
     */
    public static boolean instanceOf(Object obj, Class<?> cl){
    	return obj.getClass() == cl || isAssignable(obj.getClass(), cl);
    }
    /**
     * Creates a new instance of a class by a given name if possible. 
     * 
     * @param name class name including packages
     * @return a new instance of that class, or null if could not be created or was not found.
     */
    public static Object createInstance(String name){
    	try {
			return Class.forName(name).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			return null;
		}
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
	//---------------------------FILE IO----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets a {@link File} object for a file path. If any directories in the file
	 * path do not exist, they are created. If the file does not exist, it is created.
	 * 
	 * @param filename the file path
	 * @return a {@link File} object for a file path
	 */
	public static File getFile(String filename){
        File file = new File(filename);
        
        File parent = file.getAbsoluteFile().getParentFile();
        if (!parent.exists()) 
            parent.mkdirs();
        
        if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {}
        }
        
        return file;
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
	 * Gets whether or not the current operating system is osx.
	 * @return true if the current OS is windows.
	 */
	public static boolean isMac(){
		return os.indexOf("mac") >= 0;
	}
	/**
	 * Gets whether or not the current operating system is unix or linux.
	 * @return true if the current OS is windows.
	 */
	public static boolean isUnix(){
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0;
	}
	/**
	 * Gets whether or not the current operating system is solaris.
	 * @return true if the current OS is windows.
	 */
	public static boolean isSolaris(){
		return os.indexOf("sunos") >= 0;
	}
	/**
	 * Gets whether or not the current operating system is windows 7 or later.
	 * @return true if the current OS is windows 7 or later.
	 */
	public static boolean isWin7OrLater(){
		try{
			return isWindows() && Float.parseFloat(version) >= 6.1f;
		}catch(NumberFormatException e){
			return false;
		}
	}
	/**
	 * Gets whether or not the current operating system is windows vista or later.
	 * @return true if the current OS is windows vista or later.
	 */
	public static boolean isWinVistaOrLater(){
		try{
			return isWindows() && Float.parseFloat(version) >= 6.0f;
		}catch(NumberFormatException e){
			return false;
		}
	}
	/**
	 * Gets whether or not the current operating system architecture is 64bit.
	 * @return true if the current OS architecture is 64bit.
	 */
	public static boolean isArchitectureX64(){
		return architecture.indexOf("64") >= 0;
	}
	/**
	 * Gets whether or not the current operating system architecture is 32bit.
	 * @return true if the current OS architecture is 32bit.
	 */
	public static boolean isArchitectureX86(){
		return architecture.indexOf("86") >= 0;
	}
}
