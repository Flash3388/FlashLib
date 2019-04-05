package com.flash3388.flashlib.math;

import com.jmath.vectors.Vector3;

/**
 * Mathf is FlashLib's math utilities class. It is not meant to replace java's {@link Math}, but
 * rather expend upon it with additional utilities. This class cannot be instantiated and all
 * methods are static.
 * <p>
 * Methods in this class handle many math topics, including by not limited to: integrals, derivatives,
 * complex numbers, vectors, matrices and much more.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Mathf {
	private Mathf(){}
	
	/**
	 * Calculates the average value of an array of double.
	 * 
	 * @param ds an array of double values
	 * @return the average of the array
	 */
	public static double avg(double...ds){
		double res = 0;

		for (double value : ds) {
            res += value;
        }

		return res / ds.length;
	}

	/**
	 * Gets whether or not a double value is valid, meaning that the value is finite and
	 * is a number. 
	 * @param d value to check
	 * @return true if the value is valid
	 * @see Double#isNaN(double)
	 * @see Double#isFinite(double)
	 */
	public static boolean validDouble(double d){
		return !Double.isNaN(d) && Double.isFinite(d);
	}
	
	/**
	 * Limits a given degree to a range of 0 to 360. The given angle must be in degrees.
	 * <p>
	 * If the angle is between 0 and 360 it will be returned. Otherwise, if its absolute value is bigger 
	 * than 360, the angle will be reduced to the corresponding value between 0 and 360 according to the unit
	 * circle. If the angle is negative, its value will be changed to a corresponding positive angle according
	 * to the unit circle.
	 * 
	 * @param value An angle in degrees to limit.
	 * @return The value of the angle after being limited.
	 */
	public static double translateAngle(double value){
		value %= 360;

		if(value < 0) {
            value += 360;
        }

		return value;
	}

	/**
	 * Translates a given value to within an enclosed range. This translation insures that values
	 * are repeated within a range instead of overflowing.
	 * <p>
	 * The translation is done by finding the remainder of the division between the value and
	 * the range. 
	 * <p>
	 * It is possible to force the value into staying in a positive scale, making the range
	 * between 0 and the given range value. If positive values are not force, the actual range
	 * is -range - range.
	 * 
	 * @param value a value to be translated
	 * @param range the translation range value (maximum value in range)
	 * @param forcePositive true if actual range is 0 - range, false if -range - range.
	 * @return the value after translation
	 */
	public static double translateInRange(double value, double range, boolean forcePositive){
		value %= range;

		if(forcePositive && value < 0) {
            value += range;
        }

		return value;
	}
	
	/**
	 * Insures that a given value is within a given limit. If the value is outside that limit, its value
	 * will be changed to meet the limit accordingly:
	 * <ul>
	 * 	<li>value bigger than max : value = max</li>
	 * 	<li>value smaller than min : value = min</li>
	 * </ul>
	 * 
	 * @param value The value to limit
	 * @param min The minimum limit
	 * @param max The maximum limit
	 * @return The new value after making sure it is within the given limit.
	 */
	public static double constrain(double value, double min, double max){
		if(value > max) {
		    value = max;
        } else if(value < min) {
		    value = min;
        }

		return value;
	}

	/**
	 * Makes sure that a given value is within a given limit. If the value is outside that limit, its value
	 * will be changed to meet the limit accordingly:
	 * value bigger than max : value = max
	 * value smaller than min : value = min
	 * <p>
	 * Compensates for negative values. This is done by constraining the absolute value. If
	 * the value was initially negative, a negative value is returned, otherwise a positive number is
	 * returned.
	 * </p>
	 * 
	 * @param value The value to limit
	 * @param min The minimum limit. must be non-negative
	 * @param max The maximum limit. must be non-negative
	 * @return The new value after making sure it is within the given limit.
	 */
	public static double constrain2(double value, double min, double max){
		double mag = Math.abs(value);

		if(mag > max) {
		    mag = max;
        } else if(mag < min) {
		    mag = min;
        }

		return value >= 0? mag : -mag;
	}

	/**
	 * Gets whether a value is between two boundaries.
	 * 
	 * @param value value to check
	 * @param min lower boundary
	 * @param max upper boundary
	 * @return true if the value is limited, false otherwise
	 */
	public static boolean constrained(double value, double min, double max){
		return value >= min && value <= max;
	}
	
	/**
	 * Scales a given value between 2 boundaries. 
	 * <p>
	 * Uses Feature Scaling, a method to standardize the range of independent variables or features of data.
	 * In data processing, it is also known as data normalization and is generally performed during the data 
	 * preprocessing step.
	 * <p>
	 * This method uses rescaling of the features to range in a given boundary. The general formula:
	 * <p>
	 * {@code
	 * x` = (x - min) / (max - min)
	 * }
	 * 
	 * 
	 * @param value value to scale
	 * @param min minimum boundary
	 * @param max maximum boundary
	 * @return scaled value
	 */
	public static double scale(double value, double min, double max){
		return (value - min) / (max - min);
	}

    public static double shortestAngularDistance(double current, double last) {
        Vector3 currentVector = new Vector3(current - 360.0, current, current + 360.0);

        Vector3 diffVector = currentVector.sub(last);
        double result = diffVector.abs().min();

        if (result > 180.0) {
            result = Math.signum(result) * (360.0 - result);
        }

        return result;
    }
}
