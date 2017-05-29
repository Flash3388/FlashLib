package edu.flash3388.flashlib.testing;

import java.io.IOException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;
import edu.flash3388.flashlib.vision.DefaultFilterCreator;
import edu.flash3388.flashlib.vision.ProcessingFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;

public class Main {

	public static void main(String[] args) {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		
		ProcessingFilter.setFilterCreator(new DefaultFilterCreator());
		VisionProcessing proc = new VisionProcessing();
		try {
			proc.parseXml("filters.xml");
		} catch (SAXException | IOException | ParserConfigurationException | XMLParseException e) {
			e.printStackTrace();
		}
		
		ProcessingFilter[] filters = proc.getFilters();
		for (ProcessingFilter filter : filters) {
			System.out.print(filter.getClass().getName()+": ");
			double[] params = filter.getParameters();
			for (double d : params) {
				System.out.print(d+" ");
			}
			System.out.println();
		}
		
		proc.saveXml("filters2.xml");
	}
}
