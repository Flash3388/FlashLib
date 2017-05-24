package edu.flash3388.flashlib.util;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FlashUtil {
	
	public static final String VERSION = "0.1.0";
	
	private FlashUtil(){}
	private static long startTime = 0;
	private static Log mainLog;
	private static ExecutorService executor;
	

	
	//--------------------------------------------------------------------
	//-----------------------General--------------------------------------
	//--------------------------------------------------------------------
	
	public static void delay(long ms){
		if(ms <= 0) return;
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {}
	}
	public static void delay(double secs){
		delay((long)(secs * 1000));
	}
	public static long millis(){
		return startTime != 0? System.currentTimeMillis() - startTime : -1;
	}
	public static int millisInt(){
		return (int) (startTime != 0? System.currentTimeMillis() - startTime : -1);
	}
	public static double secs(){
		return startTime != 0? millis() / 1000.0 : -1;
	}

	
	protected static void setStartTime(long time){
		if(startTime == 0)
			startTime = time;
	}
	public static void setStart(Log.LoggingType logType, boolean overrideLog){
		setStartTime(System.currentTimeMillis());
		if(mainLog == null)
			mainLog = new Log("flashlib", logType, overrideLog);
	}
	public static void setStart(){
		setStart(Log.LoggingType.Stream, false);
	}
	public static Log getLog(){
		return mainLog;
	}

	//--------------------------------------------------------------------
	//-----------------------Executor-------------------------------------
	//--------------------------------------------------------------------

	private static void initExecutor(){
		if (executor == null)
			executor = Executors.newCachedThreadPool();
	}
	private static boolean isExecutorInit(){
		return executor != null;
	}
	
	public static void awaitExecutorTermination(){
		if(isExecutorInit()) return;
		executor.shutdown();
	}
	public static void terminateExecutor(){
		if(isExecutorInit()) return;
		executor.shutdownNow();
	}
	public static boolean isExecutorShutdown(){
		return isExecutorInit() && executor.isShutdown();
	}
	
	public static void execute(Runnable r){
		initExecutor();
		executor.execute(r);
	}
	public static <T> T executeForTime(Callable<T> callable, long ms){
		initExecutor();
		
		Future<T> future = executor.submit(callable); 
		try {
			return future.get(ms, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			return null;
		}
	}

	//--------------------------------------------------------------------
	//--------------------------Arrays------------------------------------
	//--------------------------------------------------------------------
	
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }
	
    //------------is empty---------------
    
    public static boolean isEmpty(byte[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(short[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(int[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(long[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(float[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(double[] arr) {
        return arr == null || arr.length == 0; 
    }
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0; 
    }
    
    //------------index of---------------
    
	public static int indexOf(byte[] data, int start, int end, byte ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(short[] data, int start, int end, short ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(int[] data, int start, int end, int ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(long[] data, int start, int end, long ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(double[] data, int start, int end, double ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(float[] data, int start, int end, float ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(char[] data, int start, int end, char ch){
		for (int i = start; i <= end; i++) 
			if(data[i] == ch) return i;
		return -1;
	}
	public static int indexOf(Object[] data, int start, int end, Object ch){
		for (int i = start; i <= end; i++) 
			if(data[i].equals(ch)) return i;
		return -1;
	}
	
	//------------contains---------------
	
	public static boolean arrayContains(byte[] data, int start, int end, byte ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(short[] data, int start, int end, short ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(int[] data, int start, int end, int ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(long[] data, int start, int end, long ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(float[] data, int start, int end, float ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(double[] data, int start, int end, double ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(char[] data, int start, int end, char ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	public static boolean arrayContains(Object[] data, int start, int end, Object ch){
		return indexOf(data, start, end, ch) >= 0;
	}
	
	//------------print---------------
	
	public static void printBytes(byte[] s){
		for(byte i : s)
			System.out.print((char)i + " ");
	}
	public static void printArray(byte[] s){
		for(byte i : s)
			System.out.println((int)i);
	}
	public static void printArray(short[] s){
		for(short sh : s)
			System.out.println(sh);
	}
	public static void printArray(int[] s){
		for(int i : s)
			System.out.println(i);
	}
	public static void printArray(long[] s){
		for(float l : s)
			System.out.println(l);
	}
	public static void printArray(double[] s){
		for(double d : s)
			System.out.println(d);
	}
	public static void printArray(float[] s){
		for(float f : s)
			System.out.println(f);
	}
	public static void printArray(char[] s){
		for(char c : s)
			System.out.println(c);
	}
	public static void printArray(Object[] s){
		for(Object str : s)
			System.out.println(str);
	}
	
	//------------shift left---------------
	
	public static void shiftArrayL(byte[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(short[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(int[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(long[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(double[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(float[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(char[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	public static void shiftArrayL(Object[] arr, int start, int end){
		if(start > end || end > arr.length || start > arr.length || start < 0 || end < 0)
			throw new IllegalArgumentException("Illegal shift arguments");
		for (int i = start; i < end; i++) 
			arr[i] = arr[i+1];
	}
	
	//------------resize---------------
	
	public static byte[] resize(byte[] arr, int newSize){
		byte[] nArr = new byte[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	public static short[] resize(short[] arr, int newSize){
		short[] nArr = new short[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	public static int[] resize(int[] arr, int newSize){
		int[] nArr = new int[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	public static long[] resize(long[] arr, int newSize){
		long[] nArr = new long[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr; 
	}
	public static float[] resize(float[] arr, int newSize){
		float[] nArr = new float[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	public static double[] resize(double[] arr, int newSize){
		double[] nArr = new double[newSize];
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr; 
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] resize(T[] arr, int newSize){
		Class<?> type = arr.getClass().getComponentType();
		T[] nArr = (T[]) Array.newInstance(type, newSize);
		System.arraycopy(arr, 0, nArr, 0, Math.min(newSize, arr.length));
		return nArr;
	}
	
	//------------copy---------------
	
	public static byte[] copy(byte[] arr){
		byte[] nArr = new byte[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	public static short[] copy(short[] arr){
		short[] nArr = new short[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	public static int[] copy(int[] arr){
		int[] nArr = new int[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	public static long[] copy(long[] arr){
		long[] nArr = new long[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr; 
	}
	public static float[] copy(float[] arr){
		float[] nArr = new float[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	public static double[] copy(double[] arr){
		double[] nArr = new double[arr.length];
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr; 
	}
	@SuppressWarnings("unchecked")
	public static <T> T[] copy(T[] arr){
		Class<?> type = arr.getClass().getComponentType();
		T[] nArr = (T[]) Array.newInstance(type, arr.length);
		System.arraycopy(arr, 0, nArr, 0, arr.length);
		return nArr;
	}
	
	
	public static double[] toDoubleArray(String[] str){
		double[] d = new double[str.length];
		for (int i = 0; i < d.length; i++) 
			d[i] = FlashUtil.toDouble(str[i]);
		return d;
	}
	public static String[] toStringArray(double[] d){
		String[] str = new String[d.length];
		for (int i = 0; i < d.length; i++) 
			str[i] = String.valueOf(d[i]);
		return str;
	}

	//--------------------------------------------------------------------
	//--------------------------Conversion--------------------------------
	//--------------------------------------------------------------------
	
	public static byte[] fillByteArray(double value, byte[] bytes){
		return fillByteArray(value, 0, bytes);
	}
	public static byte[] fillByteArray(double value, int start, byte[] bytes){
		if(bytes.length < 8) throw new IllegalArgumentException("Bytes array must be 8 bytes long");
		
		long lng = Double.doubleToLongBits(value);
		for(int i = 0; i < 8; i++) 
			bytes[start + i] = (byte)((lng >> ((7 - i) * 8)) & 0xff);
		return bytes;
	}
	public static byte[] fillByteArray(int value, byte[] bytes){
		return fillByteArray(value, 0, bytes);
	}
	public static byte[] fillByteArray(int value, int start, byte[] bytes){
		if(bytes.length < 4) throw new IllegalArgumentException("Bytes array must be 4 bytes long");
		
		bytes[start + 3] = (byte) (value & 0xff);   
		bytes[start + 2] = (byte) ((value >> 8) & 0xff);   
		bytes[start + 1] = (byte) ((value >> 16) & 0xff);   
		bytes[start] = (byte) ((value >> 24) & 0xff);
		return bytes;
	}
	public static void fillByteArray(long value, byte[] bytes){
		fillByteArray(value, 0, bytes);
	}
	public static void fillByteArray(long value, int start, byte[] bytes) {
	    for (int i = start + 7; i >= start; i--) {
	    	bytes[i] = (byte)(value & 0xFF);
	        value >>= 8;
	    }
	}
	
	public static byte[] toByteArray(long value){
	    byte[] bytes = new byte[8];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	public static long toLong(byte[] b) {
	    long result = 0;
	    for (int i = 0; i < 8; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	public static long toLong(byte[] b, int s) {
	    long result = 0;
	    int e = s+8;
	    for (int i = s; i < e; i++) {
	        result <<= 8;
	        result |= (b[i] & 0xFF);
	    }
	    return result;
	}
	public static byte[] toByteArray(int value){
	    byte[] bytes = new byte[4];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	public static int toInt(byte[] b){
	    return   b[3] & 0xff |
	            (b[2] & 0xff) << 8 |
	            (b[1] & 0xff) << 16 |
	            (b[0] & 0xff) << 24;
	}
	public static int toInt(byte[] b, int s){
	    return toInt(Arrays.copyOfRange(b, s, s + 4));
	}
	public static byte[] toByteArray(double value) {
	    byte[] bytes = new byte[8];
	    fillByteArray(value, bytes);
	    return bytes;
	}
	public static double toDouble(byte[] bytes) {
	    return ByteBuffer.wrap(bytes).getDouble();
	}
	public static double toDouble(byte[] b, int s) {
	    return toDouble(Arrays.copyOfRange(b, s, s + 8));
	}
	public static boolean equals(byte[] b1, byte[] b2){
		if(b1.length != b2.length) return false;
		for(int i = 0; i < b1.length; i++){
			if(b1[i] != b2[i])
				return false;
		}
		return true;
	}
	
	public static int toInt(String s){
		return toInt(s, 0);
	}
	public static int toInt(String s, int defaultVal){
		try{
			return Integer.parseInt(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	public static long toLong(String s){
		return toLong(s, 0);
	}
	public static long toLong(String s, long defaultVal){
		try{
			return Long.parseLong(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	public static double toDouble(String s){
		return toDouble(s, 0);
	}
	public static double toDouble(String s, double defaultVal){
		try{
			return Double.parseDouble(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	public static float toFloat(String s){
		return toFloat(s, 0);
	}
	public static float toFloat(String s, float defaultVal){
		try{
			return Float.parseFloat(s);
		}catch(NumberFormatException e){}
		return defaultVal;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Parsing-----------------------------------
	//--------------------------------------------------------------------
	
	public static String toDataString(String[] str, String sep){
		String s = "";
		for (int i = 0; i < str.length; i++) 
			s += str[i] + sep;
		return s.substring(0, s.length());
	}
	public static String splitAndGet(String str, String seperator, int index){
		if(index < 0)
			throw new IllegalArgumentException("Index must be non-negative");
		String[] splits = str.split(seperator);
		if(splits.length < index + 1)
			throw new ArrayIndexOutOfBoundsException("Index is out of splited array bounds");
		return splits[index];
	}
	public static Map<String, String> parseValueParameters(String line){
		String[] split = line.split(" ");
		return parseValueParameters(split);
	}
	public static Map<String, String> parseValueParameters(String[] args){
		Map<String, String> map = new HashMap<String, String>();
		for (String param : args) {
			String[] vals = param.split("=");
			if(vals.length != 2)
				continue;
			map.put(vals[0].trim(), vals[1].trim());
		}
		return map;
	}
	public static String[] parseParameters(String line){
		String[] split = line.trim().split("-");
		for (int i = 0; i < split.length; i++) {
			split[i] = split[i].trim();
		}
		return split;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Reflection--------------------------------
	//--------------------------------------------------------------------
	
	public static boolean isAssignable(Class<?> cl, Class<?> suCl){
		return suCl.isAssignableFrom(cl);
	}
	
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
    public static boolean instanceOf(Object obj, Class<?> cl){
    	return obj.getClass() == cl || isAssignable(obj.getClass(), cl);
    }
	
	//--------------------------------------------------------------------
	//----------------------Communications--------------------------------
	//--------------------------------------------------------------------
	
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
	
}
