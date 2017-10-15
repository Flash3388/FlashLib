package edu.flash3388.flashlib.math;

/**
 * Represents a mathematical function. This interface is used by {@link Mathf} for math
 * calculation which require a function.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface Function {
	
	/**
	 * Gets the function's value for a given x-value.
	 * 
	 * @param x x-value
	 * @return function's corresponding y-value.
	 */
	double f(double x);
}
