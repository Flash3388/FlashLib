package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public class Main {

	public static void main(String[] args) {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		Log log = new Log("TestLog", Log.LoggingType.Buffered, true);
		
		for (int i = 0; i < 10; i++){
			log.log("Logging...");
			log.logTime("Logging");
			log.reportError("Error");
		}
		
		long nanos = System.nanoTime();
		log.save();
		nanos = System.nanoTime() - nanos;
		System.out.println(nanos / 1e6);
		
		log.log("Logging...2");
		log.log("Logging...2");
		log.logTime("Logging2");
		
		log.close();
		
		for (int i = 0; i < 10; i++)
			FlashUtil.getLog().log("Logging "+(i));
		FlashUtil.getLog().close();
	}

}
