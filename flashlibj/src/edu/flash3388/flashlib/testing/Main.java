package edu.flash3388.flashlib.testing;

import java.io.IOException;

import javax.management.modelmbean.XMLParseException;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

public class Main {

	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException, XMLParseException {
		Log.setParentDirectory("/home/tomtzook/frc");
		FlashUtil.setStart();
		
		
	}
}
