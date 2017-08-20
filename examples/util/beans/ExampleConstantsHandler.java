package examples.util.beans;

import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

/*
 * An example for using the flashlib ConstantsHandler.
 */
public class ExampleConstantsHandler {

	public static void main(String[] args) {
		/*
		 * Loads constants from an xml file. If the file does not exist no constants will be loaded. 
		 */
		ConstantsHandler.loadConstantsFromXml("constants.xml");
		
		/*
		 * Adds a new constant to the map with the value of 50. If the constant already exists, nothing will occur.
		 * Returns a data property pointing to that value. 
		 */
		DoubleProperty addNum = ConstantsHandler.addNumber("addNum", 50.0);
		System.out.println("addNum: "+addNum.get());
		/*
		 * Sets the value in the map of the constant to 20.5. This updates the data property as well. 
		 */
		ConstantsHandler.putNumber("addNum", 20.5);
		System.out.println("addNum: "+addNum.get());
		
		/*
		 * Sets the value of a constant. If the constant does not exist, it is created. 
		 */
		ConstantsHandler.putString("putString", "a string");
		/*
		 * Gets the value of a constant, instead of a data property.
		 */
		System.out.println("putString: "+ConstantsHandler.getStringValue("putString"));
		
		ConstantsHandler.putString("putString", "new string");
		
		/*
		 * Saves the constants into an xml file. If the file does not exist, it is created.
		 */
		ConstantsHandler.saveConstantsToXml("constants.xml");
	}

}
