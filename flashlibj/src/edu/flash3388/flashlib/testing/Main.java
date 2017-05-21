package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.util.CodeTester;
import edu.flash3388.flashlib.util.CommandParser;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public class Main {

	public static void main(String[] args) {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		
		CodeTester tester = new CodeTester();
		tester.addTest(new TcpTest());
		tester.run();
	}
}
