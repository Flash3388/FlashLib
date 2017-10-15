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
	
	
	private static final double ROOT_DIFFERENCE = 1e-8;
	private static final int DEFAULT_INTEGRAL_TREPAZOIDS = 100;
	private static final int DEFAULT_INTEGRAL_SLICES = 10;
	
	//--------------------------------------------------------------------
	//--------------------------General-----------------------------------
	//--------------------------------------------------------------------
	
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
	/**
	 * Gets the result of a reverse Pythagorases theorem for a given set of numbers.
	 * 
	 * The Pythagorean theorem states that:
	 * In any right triangle, the area of the square whose side is the hypotenuse 
	 * (the side opposite the right angle) is equal to the sum of the areas of the squares whose 
	 * sides are the two legs (the two sides that meet at a right angle).
	 * 
	 * @param a value a
	 * @param c value c
	 * @return result of the reverse Pythagorases
	 */
	public static double reversePythagorasTheorem(double a, double c){
		return Math.sqrt((c * c) - (a * a));
	}

	/**
	 * Calculates the nth root of a given number and degree.
	 * <p>
	 * In mathematics, an nth root of a number x, where n is usually assumed to be a positive integer, 
	 * is a number r which, when raised to the power n yields x
	 * 
	 * @param result The result of the base in the power of exponent.
	 * @param degree The root degree
	 * @return The base who when multiplied exponent times returns the given result
	 * @throws IllegalArgumentException if result is negative
	 */
	public static double root(double result, int degree){
		if(result < 0)
			throw new IllegalArgumentException("Cannot calculate negative root! Use complexRoot instead");
        if(result == 0) 
            return 0;
        
        double x1 = result;
        double x2 = result / degree;  
        while (Math.abs(x1 - x2) > ROOT_DIFFERENCE){
            x1 = x2;
            x2 = ((degree - 1.0) * x2 + result / Math.pow(x2, degree - 1.0)) / degree;
        }
        return x2;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Complex-----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Calculates the nth root of a given number. This method considers the existence of complex numbers
	 * and will work with a negative number.
	 * <p>
	 * In mathematics, an nth root of a number x, where n is usually assumed to be a positive integer, 
	 * is a number r which, when raised to the power n yields x
	 * 
	 * @param result The result of the base in the power of exponent.
	 * @param exponent The root exponent
	 * @return The base, as a complex number, who when multiplied exponent times returns the given result
	 */
	public static Complex complexRoot(double result, int exponent){
		if(result > 0)
			return new Complex(root(result, exponent), 0);
        if(result == 0) 
            return new Complex(0, 0);
        
        result *= -1;
        double x1 = result;
        double x2 = result / exponent;  
        while (Math.abs(x1 - x2) > ROOT_DIFFERENCE){
            x1 = x2;
            x2 = ((exponent - 1.0) * x2 + result / Math.pow(x2, exponent - 1.0)) / exponent;
        }
        return new Complex(0, x2);
	}
	/**
	 * Divides a real number by a complex number.
	 * <p>
	 * This is done by multiplying the both numbers by the conjugate of the complex number, which gives
	 * us a real number as the divisor and a complex number as the dividend, we then use {@link Complex#divide(Complex)} 
	 * to get the result. 
	 * 
	 * @param n The real number
	 * @param z The complex number
	 * @return The result of the division of n by z
	 */
	public static Complex divideByComplex(double n, Complex z){
		final Complex I = new Complex(0, 1);
		return new Complex(0, n).divide(z.multiply(I));
	}
	
	/**
	 * Performs a discrete Fourier transform for an array of samples.
	 * <p>
	 * The Fourier transform decomposes a function of time (a signal) 
	 * into the frequencies that make it up, in a way similar to how a 
	 * musical chord can be expressed as the frequencies (or pitches) of its constituent notes.
	 * </p>
	 * <p>
	 * In mathematics, the discrete Fourier transform (DFT) converts a finite sequence of 
	 * equally-spaced samples of a function into an equivalent-length sequence of equally-spaced 
	 * samples of the discrete-time Fourier transform (DTFT), which is a complex-valued function of 
	 * frequency. 
	 * </p>
	 * @param samples an array of samples
	 * @return the result of the fourier transform
	 * @see <a href="https://en.wikipedia.org/wiki/Discrete_Fourier_transform">https://en.wikipedia.org/wiki/Discrete_Fourier_transform</a>
	 */
	public static Complex[] discreteFourierTransform(double... samples){
		Function func = (x)->{return samples[(int)x];};
		return discreteFourierTransform(func, samples.length);
	}
	/**
	 * Performs a discrete Fourier transform for an array of samples.
	 * <p>
	 * The Fourier transform decomposes a function of time (a signal) 
	 * into the frequencies that make it up, in a way similar to how a 
	 * musical chord can be expressed as the frequencies (or pitches) of its constituent notes.
	 * </p>
	 * <p>
	 * In mathematics, the discrete Fourier transform (DFT) converts a finite sequence of 
	 * equally-spaced samples of a function into an equivalent-length sequence of equally-spaced 
	 * samples of the discrete-time Fourier transform (DTFT), which is a complex-valued function of 
	 * frequency. 
	 * </p>
	 * @param func the function of time
	 * @param samples the amount of samples to use
	 * @return the result of the fourier transform
	 * @see <a href="https://en.wikipedia.org/wiki/Discrete_Fourier_transform">https://en.wikipedia.org/wiki/Discrete_Fourier_transform</a>
	 */
	public static Complex[] discreteFourierTransform(Function func, int samples){
		Complex[] results = new Complex[samples];
		for(int i = 0; i < samples; i++)
			results[i] = discreteFourierTransform(func, (i + 1), samples);
		return results;
	}
	/**
	 * Performs a discrete Fourier transform for an array of samples.
	 * <p>
	 * The Fourier transform decomposes a function of time (a signal) 
	 * into the frequencies that make it up, in a way similar to how a 
	 * musical chord can be expressed as the frequencies (or pitches) of its constituent notes.
	 * </p>
	 * <p>
	 * In mathematics, the discrete Fourier transform (DFT) converts a finite sequence of 
	 * equally-spaced samples of a function into an equivalent-length sequence of equally-spaced 
	 * samples of the discrete-time Fourier transform (DTFT), which is a complex-valued function of 
	 * frequency. 
	 * </p>
	 * @param func the function of time
	 * @param samples the amount of samples to use
	 * @param k the index
	 * @return the result of the fourier transform
	 * @see <a href="https://en.wikipedia.org/wiki/Discrete_Fourier_transform">https://en.wikipedia.org/wiki/Discrete_Fourier_transform</a>
	 */
	public static Complex discreteFourierTransform(Function func, int k, int samples){
		Complex result = new Complex();
		double v = 2 * Math.PI * k / samples;
		for(int i = 0; i < samples; i++)
			result.add(Complex.euler(func.f(i), v * i));
		return result;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Matrices----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Performs a multiplication between two matrices.
	 * <p>
	 * Multiplication of two matrices is defined if and only if the number of 
	 * columns of the left matrix is the same as the number of rows of the right matrix.
	 * </p>
	 * @param mat1 first matrix
	 * @param mat2 seconds matrix
	 * @return the result of the matrix multiplication
	 * @throws IllegalArgumentException if the matrices are not compatible for multiplication
	 */
	public static double[][] multiplyMat(double[][] mat1, double[][] mat2){
		if(mat1[0].length != mat2.length) 
			throw new IllegalArgumentException("Cannot multiply matricies");
		double[][] result = new double[mat2.length][mat2[0].length];
		for(int i = 0; i < result[0].length; i++){
			for(int j = 0; j < result.length; j++){
				double value = 0;
				for(int k = 0; k < result.length; k++)
					value += mat1[j][k] * mat2[k][i];
				result[j][i] = value;
			}
		}
		return result;
	}
	/**
	 * Performs a multiplication between a series of matrices. Every matrix is multiplied by the next.
	 * <p>
	 * Multiplication of two matrices is defined if and only if the number of 
	 * columns of the left matrix is the same as the number of rows of the right matrix.
	 * </p>
	 * @param mats an array if matrices
	 * @return the result of the matrix multiplication
	 * @throws IllegalArgumentException if less than 2 matrices are in the array
	 */
	public static double[][] multiplyMat(double[][]...mats){
		if(mats == null || mats.length < 2)
			throw new IllegalArgumentException("Insufficent matrices to multiply");
		
		double[][] mat = mats[0];
		for(int i = 1; i < mats.length; i++)
			mat = multiplyMat(mat, mats[i]);
		return mat;
	}
	/**
	 * Creates a 3d rotation matrix for all coordinates. 
	 * <p>
	 * In linear algebra, a rotation matrix is a matrix that is used to perform a rotation in Euclidean space.
	 * </p>
	 * @param x the x rotation value in degrees
	 * @param y the y rotation value in degrees
	 * @param z the z rotation value in degrees
	 * @return the rotation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Rotation_matrix">https://en.wikipedia.org/wiki/Rotation_matrix</a>
	 */
	public static double[][] rotationMatrix3d(double x, double y, double z){
		return multiplyMat(rotationMatrix3dX(x), rotationMatrix3dY(y), rotationMatrix3dZ(z));
	}
	/**
	 * Creates a 3d rotation matrix around the x-axis. 
	 * <p>
	 * In linear algebra, a rotation matrix is a matrix that is used to perform a rotation in Euclidean space.
	 * </p>
	 * @param angle the angle of rotation in degrees
	 * @return the rotation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Rotation_matrix">https://en.wikipedia.org/wiki/Rotation_matrix</a>
	 */
	public static double[][] rotationMatrix3dX(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{1, 0, 0, 0},
			{0, Math.cos(angle), -Math.sin(angle), 0},
			{0, Math.sin(angle), Math.cos(angle), 0},
			{0,0,0,1}
		};
	}
	/**
	 * Creates a 3d rotation matrix around the y-axis. 
	 * <p>
	 * In linear algebra, a rotation matrix is a matrix that is used to perform a rotation in Euclidean space.
	 * </p>
	 * @param angle the angle of rotation in degrees
	 * @return the rotation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Rotation_matrix">https://en.wikipedia.org/wiki/Rotation_matrix</a>
	 */
	public static double[][] rotationMatrix3dY(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), 0, Math.sin(angle),0},
			{0, 1, 0,0},
			{-Math.sin(angle), 0, Math.cos(angle),0},
			{0,0,0,1}
		};
	}
	/**
	 * Creates a 3d rotation matrix around the z-axis. 
	 * <p>
	 * In linear algebra, a rotation matrix is a matrix that is used to perform a rotation in Euclidean space.
	 * </p>
	 * @param angle the angle of rotation in degrees
	 * @return the rotation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Rotation_matrix">https://en.wikipedia.org/wiki/Rotation_matrix</a>
	 */
	public static double[][] rotationMatrix3dZ(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), -Math.sin(angle), 0,0},
			{Math.sin(angle), Math.cos(angle), 0,0},
			{0,0,1,0},
			{0,0,0,1}
		};
	}
	/**
	 * Creates a 2d rotation matrix around the z-axis. 
	 * <p>
	 * In linear algebra, a rotation matrix is a matrix that is used to perform a rotation in Euclidean space.
	 * </p>
	 * @param angle the angle of rotation in degrees
	 * @return the rotation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Rotation_matrix">https://en.wikipedia.org/wiki/Rotation_matrix</a>
	 */
	public static double[][] rotationMatrix2d(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), -Math.sin(angle), 0},
			{Math.sin(angle), Math.cos(angle), 0},
			{0, 0, 1},
		};
	}
	
	/**
	 * Creates a 3d transformation matrix for all coordinates. 
	 * <p>
	 * In linear algebra, linear transformations can be represented by matrices.
	 * </p>
	 * @param x the x transformation value
	 * @param y the y transformation value
	 * @param z the z transformation value
	 * @return the transformation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Transformation_matrix">https://en.wikipedia.org/wiki/Transformation_matrix</a>
	 */
	public static double[][] translationMatrix3d(double x, double y, double z){
		return new double[][]{
			{1,0,0,x},
			{0,1,0,y},
			{0,0,1,z},
			{0,0,0,1}
		};
	}
	/**
	 * Creates a 2d transformation matrix for all coordinates. 
	 * <p>
	 * In linear algebra, linear transformations can be represented by matrices.
	 * </p>
	 * @param x the x transformation value
	 * @param y the y transformation value
	 * @return the transformation matrix
	 * @see <a href="https://en.wikipedia.org/wiki/Transformation_matrix">https://en.wikipedia.org/wiki/Transformation_matrix</a>
	 */
	public static double[][] translationMatrix2d(double x, double y){
		return new double[][]{
			{1,0,x},
			{0,1,y},
			{0,0,1}
		};
	}
	
	/**
	 * Reverses the values in the matrix by multiplying it by -1.
	 * @param mat mat to reverse
	 */
	public static void reverseMatrixValues(double[][] mat){
		for(int i = 0; i < mat.length; i++){
			for(int j = 0; j < mat[0].length; j++)
				mat[i][j] *= -1;
		}
	}
	/**
	 * Creates a matrix with values reversed from the given matrix.
	 * @param mat mat to use values of
	 * @return a new matrix with reversed values
	 */
	public static double[][] reversedMatrix(double[][] mat){
		double[][] mat2 = new double[mat.length][mat[0].length];
		for(int i = 0; i < mat.length; i++){
			for(int j = 0; j < mat[0].length; j++)
				mat2[i][j] = -mat[i][j];
		}
		return mat2;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Derivatives---------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Derives a function for an x-coordinate. Uses the first method of central difference.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="https://en.wikipedia.org/wiki/Derivative">https://en.wikipedia.org/wiki/Derivative</a>
	 * @see #centralDifference(Function, double)
	 */
	public static double derive(Function func, double x){
		return centralDifference(func, x);
	}
	/**
	 * Derives a function for an x-coordinate. Uses the second method of central difference.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="https://en.wikipedia.org/wiki/Derivative">https://en.wikipedia.org/wiki/Derivative</a>
	 * @see #centralDifference2(Function, double)
	 */
	public static double derive2(Function func, double x){
		return centralDifference2(func, x);
	}
	
	/**
	 * Derives a function for an x-coordinate. Uses the method of forward difference. With a
	 * difference constant of 1e-8.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/ForwardDifference.html">http://mathworld.wolfram.com/ForwardDifference.html</a>
	 */
	public static double forwardDifference(Function func, double x){
		return forwardDifference(func, x, 1e-8);
	}
	/**
	 * Derives a function for an x-coordinate. Uses the method of forward difference. With a
	 * given difference constant.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @param changeConstant differance constant
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/ForwardDifference.html">http://mathworld.wolfram.com/ForwardDifference.html</a>
	 */
	public static double forwardDifference(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - func.f(x)) / changeConstant;
	}
	
	/**
	 * Derives a function for an x-coordinate. Uses the method of backward difference. With a
	 * difference constant of 1e-8.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/BackwardDifference.html">http://mathworld.wolfram.com/BackwardDifference.html</a>
	 */
	public static double backwardDifference(Function func, double x){
		return backwardDifference(func, x, 1e-8);
	}
	/**
	 * Derives a function for an x-coordinate. Uses the method of backward difference. With a
	 * given difference constant.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @param changeConstant differance constant
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/BackwardDifference.html">http://mathworld.wolfram.com/BackwardDifference.html</a>
	 */
	public static double backwardDifference(Function func, double x, double changeConstant){
		return (func.f(x) - func.f(x - changeConstant)) / changeConstant;
	}
	
	/**
	 * Derives a function for an x-coordinate. Uses the first method of central difference. With a
	 * difference constant of 1e-8.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/CentralDifference.html">http://mathworld.wolfram.com/CentralDifference.html</a>
	 */
	public static double centralDifference(Function func, double x){
		return centralDifference(func, x, 1e-8);
	}
	/**
	 * Derives a function for an x-coordinate. Uses the first method of central difference. With a
	 * given difference constant.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @param changeConstant differance constant
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/CentralDifference.html">http://mathworld.wolfram.com/CentralDifference.html</a>
	 */
	public static double centralDifference(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - func.f(x - changeConstant)) / (2 * changeConstant);
	}
	
	/**
	 * Derives a function for an x-coordinate. Uses the second method of central difference. With a
	 * difference constant of 1e-8.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/CentralDifference.html">http://mathworld.wolfram.com/CentralDifference.html</a>
	 */
	public static double centralDifference2(Function func, double x){
		return centralDifference2(func, x, 1e-8);
	}
	/**
	 * Derives a function for an x-coordinate. Uses the second method of central difference. With a
	 * given difference constant.
	 * <p>
	 * The derivative of a function of a real variable measures the sensitivity to 
	 * change of the function (output) value with respect to a change in its argument (input value).
	 * </p>
	 * @param func the function to derive
	 * @param x the x coordinate
	 * @param changeConstant differance constant
	 * @return the result of the derivative 
	 * @see <a href="http://mathworld.wolfram.com/CentralDifference.html">http://mathworld.wolfram.com/CentralDifference.html</a>
	 */
	public static double centralDifference2(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - 2 * func.f(x) + func.f(x - changeConstant)) / (changeConstant * changeConstant);
	}
	
	//--------------------------------------------------------------------
	//--------------------------Integrals---------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Integrates a function between 2 x coordinates. Uses Simpson's rule for integrals.
	 * <p>
	 * In mathematics, an integral assigns numbers to functions in a way that can 
	 * describe displacement, area, volume, and other concepts that arise by combining 
	 * infinitesimal data.
	 * </p>
	 * @param func the function to integrate
	 * @param min the lower boundary for integration
	 * @param max the upper boundary for integration
	 * @return the result of the integration
	 * @see <a href="https://en.wikipedia.org/wiki/Integral">https://en.wikipedia.org/wiki/Integral</a>
	 * @see #simpsonsRule(Function, double, double)
	 */
	public static double integrate(Function func, double min, double max){
		return simpsonsRule(func, min, max);
	}
	
	/**
	 * Integrates a function between 2 x coordinates. Uses trapezoidal rule for integrals. Uses
	 * a default count trapezoidal {@link #DEFAULT_INTEGRAL_TREPAZOIDS}.
	 * <p>
	 * In mathematics, an integral assigns numbers to functions in a way that can 
	 * describe displacement, area, volume, and other concepts that arise by combining 
	 * infinitesimal data.
	 * </p>
	 * @param func the function to integrate
	 * @param min the lower boundary for integration
	 * @param max the upper boundary for integration
	 * @return the result of the integration
	 * @see <a href="https://en.wikipedia.org/wiki/Integral">https://en.wikipedia.org/wiki/Integral</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Trapezoidal_rule">https://en.wikipedia.org/wiki/Trapezoidal_rule</a>
	 */
	public static double trapezoidalRule(Function func, double min, double max){
		return trapezoidalRule(func, min, max, DEFAULT_INTEGRAL_TREPAZOIDS);
	}
	/**
	 * Integrates a function between 2 x coordinates. Uses trapezoidal rule for integrals. Uses
	 * a given amount of trapezoids.
	 * <p>
	 * In mathematics, an integral assigns numbers to functions in a way that can 
	 * describe displacement, area, volume, and other concepts that arise by combining 
	 * infinitesimal data.
	 * </p>
	 * @param func the function to integrate
	 * @param min the lower boundary for integration
	 * @param max the upper boundary for integration
	 * @param trapezoids the count of trapezoids
	 * @return the result of the integration
	 * @see <a href="https://en.wikipedia.org/wiki/Integral">https://en.wikipedia.org/wiki/Integral</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Trapezoidal_rule">https://en.wikipedia.org/wiki/Trapezoidal_rule</a>
	 */
	public static double trapezoidalRule(Function func, double min, double max, int trapezoids){
		double h = (max - min) / trapezoids;
		double s = 0.5 * (func.f(min) + func.f(max));
		for(int i = 1; i < trapezoids; i++)
			s += func.f(min + i * h); 
		return (s * h);
	}
	
	/**
	 * Integrates a function between 2 x coordinates. Uses Simpson's rule for integrals. Uses the
	 * default amount of slices {@link #DEFAULT_INTEGRAL_SLICES}.
	 * <p>
	 * In mathematics, an integral assigns numbers to functions in a way that can 
	 * describe displacement, area, volume, and other concepts that arise by combining 
	 * infinitesimal data.
	 * </p>
	 * @param func the function to integrate
	 * @param min the lower boundary for integration
	 * @param max the upper boundary for integration
	 * @return the result of the integration
	 * @see <a href="https://en.wikipedia.org/wiki/Integral">https://en.wikipedia.org/wiki/Integral</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Simpson%27s_rule">https://en.wikipedia.org/wiki/Simpson%27s_rule</a>
	 */
	public static double simpsonsRule(Function func, double min, double max){
		return simpsonsRule(func, min, max, DEFAULT_INTEGRAL_SLICES);
	}
	/**
	 * Integrates a function between 2 x coordinates. Uses Simpson's rule for integrals. Uses a given
	 * amount of slices.
	 * <p>
	 * In mathematics, an integral assigns numbers to functions in a way that can 
	 * describe displacement, area, volume, and other concepts that arise by combining 
	 * infinitesimal data.
	 * </p>
	 * @param func the function to integrate
	 * @param min the lower boundary for integration
	 * @param max the upper boundary for integration
	 * @param slices the amount of slices
	 * @return the result of the integration
	 * @see <a href="https://en.wikipedia.org/wiki/Integral">https://en.wikipedia.org/wiki/Integral</a>
	 * @see <a href="https://en.wikipedia.org/wiki/Simpson%27s_rule">https://en.wikipedia.org/wiki/Simpson%27s_rule</a>
	 */
	public static double simpsonsRule(Function func, double min, double max, int slices){
		double h = (max - min) / slices;
		double s = func.f(min) + func.f(max), s1 = 0, s2 = 0;
		for(int i = 1; i <= slices / 2; i++){
			s1 += func.f(min + (2 * i - 1) * h);
			if(i < slices / 2) s2 += func.f(min + 2 * i * h);
		}
		return (1 / 3.0) * h * (s + 4 * s1 + 2 * s2);
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
