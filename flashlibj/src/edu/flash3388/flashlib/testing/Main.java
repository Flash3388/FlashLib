package edu.flash3388.flashlib.testing;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.LargestFilter;
import edu.flash3388.flashlib.vision.ProcessingFilter;
import edu.flash3388.flashlib.vision.RatioFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;

public class Main {

	public static void main(String[] args) {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		
		ProcessingFilter.setFilterCreator(new DefaultFilterCreator());
		VisionProcessing proc = new VisionProcessing();
		proc.addFilters(
				new ColorFilter(true, 0, 180, 0, 255, 230, 255),
				new LargestFilter(10),
				new RatioFilter(2.0, 1.0, 25.0 / 10.0, 1.0, 0.5, 0.1, 1000, 5, 1000, 5)
				);
		proc.saveXml("filters_2017.xml");
	}
}
