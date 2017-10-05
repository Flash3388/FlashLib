package examples.util.beans;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.PropertyHandler;

/*
 * An example for using the flashlib PropertyHandler.
 */
public class ExamplePropertyHandler {

	public static void main(String[] args) {
		/*
		 * Loads properties from an xml file. If the file does not exist no properties will be loaded. 
		 */
		PropertyHandler.loadPropertyFromXml("props.xml");
		
		/*
		 * Adds a new property to the map with the value of 50. If the property already exists, nothing will occur.
		 * Returns a data property pointing to that value. 
		 */
		DoubleProperty addNum = PropertyHandler.addNumber("addNum", 50.0);
		System.out.println("addNum: "+addNum.get());
		/*
		 * Sets the value in the map of the property to 20.5. This updates the data property as well. 
		 */
		PropertyHandler.putNumber("addNum", 20.5);
		System.out.println("addNum: "+addNum.get());
		
		/*
		 * Sets the value of a property. If the property does not exist, it is created. 
		 */
		PropertyHandler.putString("putString", "a string");
		/*
		 * Gets the value of a property, instead of a data property.
		 */
		System.out.println("putString: "+PropertyHandler.getStringValue("putString"));
		
		PropertyHandler.putString("putString", "new string");
		
		/*
		 * Saves the propertys into an xml file. If the file does not exist, it is created.
		 */
		PropertyHandler.savePropertiesToXml("props.xml");
	}

}
