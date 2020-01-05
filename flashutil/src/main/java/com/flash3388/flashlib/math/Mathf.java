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
public final class Mathf {
	private Mathf(){}

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
