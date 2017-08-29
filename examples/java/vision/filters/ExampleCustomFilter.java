package examples.vision.filters;

import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.IntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleBooleanProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleIntegerProperty;
import edu.flash3388.flashlib.util.beans.SimpleStringProperty;
import edu.flash3388.flashlib.util.beans.StringProperty;
import edu.flash3388.flashlib.vision.VisionFilter;
import edu.flash3388.flashlib.vision.VisionSource;

/*
 * This class show cases the requirements of creating a custom VisionFilter. 
 * 
 * The first step would be to extends VisionFilter class and implement the required method.
 * There is one required methods: process. This method receives a VisionSource object and must use
 * it to filter out data on the image. It is important to remember that the implementation of VisionSource
 * might change depending on the vision library used, so it is a good idea to keep the logic general and not
 * library-specific.
 * 
 * Each VisionFilter can hold parameters, like any class. But when working with xml or remote data, it is necessary
 * to implement those parameters in a certain way so as to allow them to be saved and loaded using streams. 
 * Each parameters has to be a Property from util.beans. 
 * There are 4 available options for parameter types:
 * 		IntegerProperty
 * 		DoubleProperty
 * 		BooleanProperty
 * 		StringProperty
 * Each parameter will require a method which returns its property. The method name has to be the parameter's name
 * and Property. For example, an amount parameter's property method will be named: amountProperty, and return
 * it's IntegerProperty.
 * If you do not declare a property method accordingly, the parameter will not be recognized when using streams.
 * 
 * Each filter should have 2 base constructors: full and default. The full will receive all the
 * required parameters and will be used by users manually. The default will receive 0 parameters and
 * will be used by data streams for initialization.
 */
public class ExampleCustomFilter extends VisionFilter{

	/*
	 * An parameter with an integer value.
	 * It is possible to use any implementation of IntegerProperty you wish, but
	 * it must be an implementation of IntegerProperty.
	 */
	private IntegerProperty prop1 = new SimpleIntegerProperty();
	/*
	 * An parameter with a double value.
	 * It is possible to use any implementation of DoubleProperty you wish, but
	 * it must be an implementation of DoubleProperty.
	 */
	private DoubleProperty prop2 = new SimpleDoubleProperty();
	/*
	 * An parameter with a boolean value.
	 * It is possible to use any implementation of BooleanProperty you wish, but
	 * it must be an implementation of BooleanProperty.
	 */
	private BooleanProperty prop3 = new SimpleBooleanProperty();
	/*
	 * An parameter with a string value.
	 * It is possible to use any implementation of StringProperty you wish, but
	 * it must be an implementation of StringProperty.
	 */
	private StringProperty prop4 = new SimpleStringProperty();
	
	/*
	 * The full constructor will be used by users when initializing the filter. This is not a must, be
	 * it is nice for users.
	 */
	public ExampleCustomFilter(int prop1, double prop2, boolean prop3, String prop4) {
		this.prop1.set(prop1);
		this.prop2.set(prop2);
		this.prop3.set(prop3);
		this.prop4.set(prop4);
		
		/*
		 * Further initialization code
		 */
	}
	/*
	 * The default constructor which will mainly be used by the data streams when they initialize 
	 * the filter. This should set all properties to their wanted default value.
	 */
	public ExampleCustomFilter() {
		this(0, 0.0, false, "");
	}
	
	
	/*
	 * Property method for the first parameter. Notice the name corresponds to the parameter name + "Property".
	 * This method returns an IntegerProperty, since the parameter uses an integer value.
	 */
	public IntegerProperty prop1Property(){
		return prop1;
	}
	/*
	 * Property method for the second parameter. Notice the name corresponds to the parameter name + "Property".
	 * This method returns an DoubleProperty, since the parameter uses a double value.
	 */
	public DoubleProperty prop2Property(){
		return prop2;
	}
	/*
	 * Property method for the third parameter. Notice the name corresponds to the parameter name + "Property".
	 * This method returns an IntegerProperty, since the parameter uses a boolean value.
	 */
	public BooleanProperty prop3Property(){
		return prop3;
	}
	/*
	 * Property method for the fourth parameter. Notice the name corresponds to the parameter name + "Property".
	 * This method returns an StringProperty, since the parameter uses a string value.
	 */
	public StringProperty prop4Property(){
		return prop4;
	}
	
	/*
	 * The process method which is called to execute the filter's logic. Implement this
	 * as you wish.
	 */
	@Override
	public void process(VisionSource source) {
	}
}
