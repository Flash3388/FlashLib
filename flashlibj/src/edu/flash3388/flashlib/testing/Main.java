package edu.flash3388.flashlib.testing;

import java.io.IOException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.LargestFilter;
import edu.flash3388.flashlib.vision.ProcessingFilter;
import edu.flash3388.flashlib.vision.RatioFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;

public class Main {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, XMLParseException {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		
		ProcessingFilter.setFilterCreator(new DefaultFilterCreator());
		VisionProcessing proc = new VisionProcessing();
		proc.addFilters(
				new ColorFilter(true, 0, 180, 0, 255, 230, 255),
				new LargestFilter(10),
				new RatioFilter(2.0, 1.0, 25.0 / 10.0, 1.0, 0.5, 0.1, 1000, 5, 1000, 5)
				);
		
		byte[] bytes = proc.toBytes();
		
		VisionProcessing proc2 = VisionProcessing.createFromBytes(bytes);
		proc2.saveXml("filters.xml");
	}
}
