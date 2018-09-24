package edu.flash3388.flashlib.math;

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
		for (int i = 0; i < ds.length; i++) 
			res += ds[i];
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
		if(value < 0)
			value += 360;
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
	public static double translate(double value, double range, boolean forcePositive){
		value %= range;
		if(forcePositive && value < 0)
			value += range;
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
		if(value > max) value = max;
		else if(value < min) value = min;
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
		if(mag > max) mag = max;
		else if(mag < min) mag = min;
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
	
	/**
	 * Rounds a decimal number to 2 numbers after the decimal point.
	 * 
	 * @param x A decimal value to round
	 * @return The rounded value
	 */
	public static double roundDecimal(double x){
		return roundDecimal(x, 2);
	}
	/**
	 * Rounds a decimal number to a given amount of numbers after the decimal point.
	 * @param x A decimal value to round
	 * @param decimalNums Amount of numbers after the decimal point
	 * @return The rounded value
	 */
	public static double roundDecimal(double x, int decimalNums){
		double m = Math.pow(10, decimalNums);
		return  Math.round(x * m) / m;
	}
	/**
	 * Rounds a number to the closest multiplier of a value.
	 * @param val a value to round
	 * @param multiplier multiplier to round to
	 * @return The rounded value
	 */
	public static double roundToMultiplier(double val, double multiplier){
		return multiplier * Math.round(val / multiplier);
	}
	/**
	 * Rounds a number to the closest multiplier of a value. 
	 * @param val a value to round
	 * @param multiplier multiplier to round to
	 * @param up true to round the value upwards, false for downwards
	 * @return The rounded value
	 */
	public static double roundToMultiplier(double val, double multiplier, boolean up){
		double rounded = roundToMultiplier(val, multiplier);
		if(rounded < val)
			rounded += up? multiplier : -multiplier;
		return rounded;
	}
	/**
	 * Gets the result of Pythagorases theorem for a given set of numbers.
	 * 
	 * The Pythagorean theorem states that:
	 * In any right triangle, the area of the square whose side is the hypotenuse 
	 * (the side opposite the right angle) is equal to the sum of the areas of the squares whose 
	 * sides are the two legs (the two sides that meet at a right angle).
	 * 
	 * This method implements it in a 3d space.
	 * 
	 * @param a value a
	 * @param b value b
	 * @param c value c
	 * @return The result of pythagorasTheorem for given values
	 */
	public static double pythagorasTheorem(double a, double b, double c){
		return Math.sqrt((a * a) + (b * b) + (c * c));
	}
	/**
	 * Gets the result of Pythagorases theorem for a given set of numbers.
	 * 
	 * The Pythagorean theorem states that:
	 * In any right triangle, the area of the square whose side is the hypotenuse 
	 * (the side opposite the right angle) is equal to the sum of the areas of the squares whose 
	 * sides are the two legs (the two sides that meet at a right angle).
	 * 
	 * @param a value a
	 * @param b value b
	 * @return The area of the square whose side is the hypotenuse
	 */
	public static double pythagorasTheorem(double a, double b){
		return pythagorasTheorem(a, b, 0);
	}
	
	//--------------------------------------------------------------------
	//--------------------------Vectors-----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Gets the inclination of a 3d vector. Inclination is the angle between the vector and the
	 * z-axis.
	 * <p>
	 * {@code
	 * 	inclination = arc cos(z / length)
	 * }
	 * </p>
	 * @param z z coordinate
	 * @param magnitude the length of the vector.
	 * @return the inclination of the vector
	 */
	public static double vecInclination(double z, double magnitude){
		double angle = Math.toDegrees(Math.acos(z / magnitude)); 
		return (z < 0)? -angle : angle;
	}
	/**
	 * Gets the azimuth of a 3d or 2d vector. Azimuth is the angle between the vector and the
	 * x-axis.
	 * <p>
	 * {@code
	 * 	azimuth = arc tan2(y, x)
	 * }
	 * </p>
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the azimuth of the vector
	 */
	public static double vecAzimuth(double y, double x){
		return Math.toDegrees(Math.atan2(y, x));
	}
	/**
	 * Gets the magnitude, or length of a 3d vector.
	 * <p>
	 * {@code
	 * 	length = sqrt(pow(x,2) + pow(y,2) + pow(z,2))
	 * }
	 * </p>
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 * @return the length of the vector
	 */
	public static double vecMagnitude(double x, double y, double z){
		return pythagorasTheorem(x, y, z);
	}
	/**
	 * Gets the magnitude, or length of a 2d vector.
	 * <p>
	 * {@code
	 * 	length = sqrt(pow(x,2) + pow(y,2)))
	 * }
	 * </p>
	 * @param x x coordinate
	 * @param y y coordinate
	 * @return the length of the vector
	 */
	public static double vecMagnitude(double x, double y){
		return pythagorasTheorem(x, y);
	}
	
	/**
	 * Gets the x coordinate of a 3d vector.
	 * <p>
	 * {@code
	 * 	x = magnitude * sin(inclination) * cos(azimuth)
	 * }
	 * </p>
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @param inclination the inclination of the vector
	 * @return the x coordinate of the vector
	 */
	public static double vecX(double magnitude, double azimuth, double inclination){
		return (magnitude * Math.sin(Math.toRadians(inclination)) * Math.cos(Math.toRadians(azimuth)));
	}
	/**
	 * Gets the x coordinate of a 2d vector.
	 * <p>
	 * {@code
	 * 	x = magnitude * cos(azimuth)
	 * }
	 * </p>
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @return the x coordinate of the vector
	 */
	public static double vecX(double magnitude, double azimuth){
		return (magnitude * Math.cos(Math.toRadians(azimuth)));
	}
	
	/**
	 * Gets the y coordinate of a 3d vector.
	 * <p>
	 * {@code
	 * 	y = magnitude * sin(inclination) * sin(azimuth)
	 * }
	 * </p>
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @param inclination the inclination of the vector
	 * @return the y coordinate of the vector
	 */
	public static double vecY(double magnitude, double azimuth, double inclination){
		return (magnitude * Math.sin(Math.toRadians(inclination)) * Math.sin(Math.toRadians(azimuth)));
	}
	/**
	 * Gets the y coordinate of a 2d vector.
	 * <p>
	 * {@code
	 * 	x = magnitude * sin(azimuth)
	 * }
	 * </p>
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @return the x coordinate of the vector
	 */
	public static double vecY(double magnitude, double azimuth){
		return (magnitude * Math.sin(Math.toRadians(azimuth)));
	}
	
	/**
	 * Gets the z coordinate of a 3d vector.
	 * <p>
	 * {@code
	 * 	z = magnitude * cos(inclination)
	 * }
	 * </p>
	 * @param magnitude the length of the vector
	 * @param inclination the inclination of the vector
	 * @return the x coordinate of the vector
	 */
	public static double vecZ(double magnitude, double inclination){
		return (magnitude * Math.cos(Math.toRadians(inclination)));
	}
}
