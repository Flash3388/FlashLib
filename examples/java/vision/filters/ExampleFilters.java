package examples.vision.filters;

import edu.flash3388.flashlib.vision.ColorFilter;
import edu.flash3388.flashlib.vision.LargestFilter;
import edu.flash3388.flashlib.vision.VisionProcessing;

/*
 * Example for creating filters
 */
public class ExampleFilters {

	public static void main(String[] args){
		/*
		 * HSV ranges for the color filter
		 */
		int min_hue = 0,
			max_hue = 180,
			min_saturation = 0,
			max_saturation = 255,
			min_value = 230,
			max_value = 255;
		
		
		/*
		 * Creates a processing object
		 */
		VisionProcessing processing = new VisionProcessing("processing test");
		
		/*
		 * Creates an HSV filter
		 */
		ColorFilter hsvFilter = new ColorFilter(true, 
				min_hue, max_hue, 
				min_saturation, max_saturation, 
				min_value, max_value);
		
		/*
		 * Creates a filter by size that saves up to 10 of the largest contours.
		 */
		LargestFilter filterLargest = new LargestFilter(10);
		
		/*
		 * Adds the filters to the processing object
		 */
		processing.addFilters(hsvFilter, filterLargest);
		
		/*
		 * Saves the processing object data to an XML file
		 */
		processing.saveXml("filters.xml");
	}
}
