package examples.util;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.util.SimpleStreamLog;

public class ExampleFlashUtil {
	
	public static void main(String[] args){
		
		//get the default log of flashlib, used many times
		Log log = FlashUtil.getLog();
		
		/*
		 * Time utilities: get the time passed since program 
		 * start or put the current thread to sleep
		 */
		//get time in ms since program start
		long millis = FlashUtil.millis();
		//get time in seconds since program start
		double secs = FlashUtil.secs();
		
		//put current thread to a 10 ms sleep
		FlashUtil.delay(10);
		//put current thread to a 1 second sleep
		FlashUtil.delay(1.0);
		
		/*
		 * Array utilities: manipulate arrays, locate value, etc
		 */
		int[] arr = {0, 2, 1, 6, 10};
		
		//get index of object in array between to indexes
		int index = FlashUtil.indexOf(arr, 0, arr.length - 1, 0);
		//check if a value is in an array between to indexes
		boolean contains = FlashUtil.arrayContains(arr, 0, arr.length - 1, 6);
		
		//create a copy of an array with the same values
		int[] copy = FlashUtil.copy(arr);
		//resize the array and keep the values
		arr = FlashUtil.resize(arr, arr.length + 5);
	
		/*
		 * Reflection utilities: use reflection to control your code
		 * and objects
		 */
		//create an instance of a class by its name
		Object o = FlashUtil.createInstance("examples.util.ExampleFlashUtil");
		//check inheritance between class types
		boolean inherits = FlashUtil.isAssignable(SimpleStreamLog.class, Log.class);
		
		/*
		 * And much more!
		 */
	}
}
