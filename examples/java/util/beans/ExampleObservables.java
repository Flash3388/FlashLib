package examples.util.beans;

import edu.flash3388.flashlib.util.beans.observable.ObservableDoubleProperty;
import edu.flash3388.flashlib.util.beans.observable.SimpleObservableDoubleProperty;

/*
 * Example of using the observable beans provided by flashlib. Unlike standard properties and value sources, 
 * observable ones allow listening and binding of values.
 */
public class ExampleObservables {

	public static void main(String[] args){
		/*
		 * Creating 2 simple double observable property. One with the default value 0.0, the other
		 * with the value of 10.0.
		 */
		ObservableDoubleProperty prop1 = new SimpleObservableDoubleProperty();
		ObservableDoubleProperty prop2 = new SimpleObservableDoubleProperty(20.0);
		
		/*
		 * Adds a new change listener to the first property using a lambda operator
		 */
		prop1.addListener((obs, oldValue, newValue)->{
			/*
			 * Prints the last value and current value
			 */
			System.out.printf("prop1 new value :: (%s, %s) %n", oldValue, newValue);
		});
		
		/*
		 * Sets the value of the first property
		 */
		prop1.set(10.0);
		
		/*
		 * Binds the first property to the second. The first property will now return the value of the seconds and
		 * will not allow changing its own value
		 */
		prop1.bind(prop2);
		/*
		 * Prints the value of the first property. Will return the value of the seconds since it is bound
		 */
		System.out.println(prop1.get());
		
		/*
		 * Sets the value of the seconds property. Notice that the listener of the first property will fire since
		 * it is bound to the second property and considers this a value change.
		 */
		prop2.set(5.0);
		
		/*
		 * Unbinds the first property from the second. It now possess its own value.
		 */
		prop1.unbind();
	}
}
