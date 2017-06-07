package edu.flash3388.flashlib.math;

public class Mathd {
	private Mathd(){}
	
	@FunctionalInterface
	public interface Function {
		double f(double x);
	}
	
	
	public static final double ROOT_DIFFERENCE = 1e-8;
	
	//--------------------------------------------------------------------
	//--------------------------General-----------------------------------
	//--------------------------------------------------------------------
	
	public static double avg(double...ds){
		double res = 0;
		for (int i = 0; i < ds.length; i++) 
			res += ds[i];
		return res / ds.length;
	}
	public static boolean validDouble(double d){
		return !Double.isNaN(d) && Double.isFinite(d);
	}
	/**
	 * Limits a given degree to a range of 0 to 360. The given angle must be in degrees.
	 * 
	 * If the angle is between 0 and 360 it will be returned. Otherwise, if its absolute value is bigger 
	 * than 360, the angle will be reduced to the corresponding value between 0 and 360 according to the unit
	 * circle. If the angle is negative, its value will be changed to a corresponding positive angle according
	 * to the unit circle.
	 * 
	 * @param value An angle in degrees to limit.
	 * @return The value of the angle after being limited.
	 */
	public static double limitAngle(double value){
		value %= 360;
		if(value < 0)
			value += 360;
		return value;
	}
	/**
	 * Makes sure that a given value is within a given limit. If the value is outside that limit, its value
	 * will be changed to meet the limit accordingly:
	 * value bigger than max : value = max
	 * value smaller than min : value = min
	 * 
	 * @param value The value to limit
	 * @param min The minimum limit
	 * @param max The maximum limit
	 * @return The new value after making sure it is within the given limit.
	 */
	public static double limit(double value, double min, double max){
		if(value > max) value = max;
		else if(value < min) value = min;
		return value;
	}
	public static boolean limited(double value, double min, double max){
		return value >= min && value <= max;
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
	 * Rounds a decimal number to a give amount of numbers after the decimal point.
	 * @param x A decimal value to round
	 * @param decimalNums Amount of numbers after the decimal point
	 * @return The rounded value
	 */
	public static double roundDecimal(double x, int decimalNums){
		double m = Math.pow(10, decimalNums);
		return  Math.round(x * m) / m;
	}
	public static double roundToMultiplier(double val, double multiplier){
		return multiplier * Math.round(val / multiplier);
	}
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
	 * @param a a
	 * @param b a
	 * @param c a
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
	 * @param a a
	 * @param b a
	 * @return The area of the square whose side is the hypotenuse
	 */
	public static double pythagorasTheorem(double a, double b){
		return pythagorasTheorem(a, b, 0);
	}
	public static double reversePythagorasTheorem(double a, double c){
		return Math.sqrt((c * c) - (a * a));
	}
	public static int multiply(int a, int b){
		return (a << (b / 2)) + ((b % 2 != 0)? a : 0);
	}
	/**
	 * Calculates the nth root of a given number.
	 * 
	 * @param result The result of the base in the power of exponent.
	 * @param exponent The root exponent
	 * @return The base who when multiplied exponent times returns the given result
	 * @throws IllegalArgumentException if result is negative
	 */
	public static double root(double result, int exponent){
		if(result < 0)
			throw new IllegalArgumentException("Cannot calculate negative root! Use complexRoot instead");
        if(result == 0) 
            return 0;
        
        double x1 = result;
        double x2 = result / exponent;  
        while (Math.abs(x1 - x2) > ROOT_DIFFERENCE){
            x1 = x2;
            x2 = ((exponent - 1.0) * x2 + result / Math.pow(x2, exponent - 1.0)) / exponent;
        }
        return x2;
	}
	
	public static double sineLaw(double a, double ratio){
		return Math.toDegrees(Math.asin(a / ratio));
	}
	public static double reverseSineLaw(double alpha, double ratio){
		return ratio * Math.sin(Math.toRadians(alpha));
	}
	public static double cosineLaw(double a, double b, double angle){
		return (a * a) + (b * b) - 2 * a * b * Math.cos(Math.toRadians(angle));
	}
	public static double reverseCosineLaw(double a, double b, double c){
		return Math.acos(-(c * c - a * a - b * b) / (2 * a * b));
	}
	public static double discriminant(double a, double b, double c){
		return (b * b) - 4 * a * c;
	}
	public static double[] quadraticFormula(double a, double b, double c){
		double root = Math.sqrt(discriminant(a, b, c));
		return new double[]{(-b + root)/(2 * a), (-b - root)/(2 * a)};
	}
	
	public static double factorial(double n){
		double result = 1;
		for (int i = 2; i <= n; i++) 
			result *= n;
		return result;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Circles-----------------------------------
	//--------------------------------------------------------------------
	
	public static double angleFromChord(double chordLength, double radius){
		return  (2 * Math.toDegrees(Math.atan(chordLength / (2 * radius))));
	}
	public static double circleArea(double radius){
		return  (Math.PI * radius * radius);
	}
	public static double circleCircumference(double radius){
		return   (2 * Math.PI * radius);
	}
	public static double circleSurfaceArea(double radius){
		return (4 * Math.PI * radius * radius);
	}
	
	//--------------------------------------------------------------------
	//--------------------------Complex-----------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Calculates the nth root of a given number. This method considers the existence of complex numbers
	 * and will work with a negative number.
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
	 * 
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
	public static Complex[] discreteFourierTransform(double... samples){
		Function func = (x)->{return samples[(int)x];};
		return discreteFourierTransform(func, samples.length);
	}
	public static Complex[] discreteFourierTransform(Function func, int samples){
		Complex[] results = new Complex[samples];
		for(int i = 0; i < samples; i++)
			results[i] = discreteFourierTransform(func, (i + 1), samples);
		return results;
	}
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
	public static double[][] multiplyMat(double[][]...mats){
		if(mats == null || mats.length < 2)
			throw new IllegalArgumentException("Insufficent matrices to multiply");
		
		double[][] mat = mats[0];
		for(int i = 1; i < mats.length; i++)
			mat = multiplyMat(mat, mats[i]);
		return mat;
	}
	public static double[][] rotationMatrix3d(double x, double y, double z){
		return multiplyMat(rotationMatrix3dX(x), rotationMatrix3dY(y), rotationMatrix3dZ(z));
	}
	public static double[][] rotationMatrix3dX(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{1, 0, 0, 0},
			{0, Math.cos(angle), -Math.sin(angle), 0},
			{0, Math.sin(angle), Math.cos(angle), 0},
			{0,0,0,1}
		};
	}
	public static double[][] rotationMatrix3dY(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), 0, Math.sin(angle),0},
			{0, 1, 0,0},
			{-Math.sin(angle), 0, Math.cos(angle),0},
			{0,0,0,1}
		};
	}
	public static double[][] rotationMatrix3dZ(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), -Math.sin(angle), 0,0},
			{Math.sin(angle), Math.cos(angle), 0,0},
			{0,0,1,0},
			{0,0,0,1}
		};
	}
	public static double[][] translationMatrix3d(double x, double y, double z){
		return new double[][]{
			{1,0,0,x},
			{0,1,0,y},
			{0,0,1,z},
			{0,0,0,1}
		};
	}
	public static double[][] rotationMatrix2d(double angle){
		angle = Math.toRadians(angle);
		return new double[][]{
			{Math.cos(angle), -Math.sin(angle), 0},
			{Math.sin(angle), Math.cos(angle), 0},
			{0, 0, 1},
		};
	}
	public static double[][] translationMatrix2d(double x, double y){
		return new double[][]{
			{1,0,x},
			{0,1,y},
			{0,0,1}
		};
	}
	public static void reverseMatrixValues(double[][] mat){
		for(int i = 0; i < mat.length; i++){
			for(int j = 0; j < mat[0].length; j++)
				mat[i][j] *= -1;
		}
	}
	public static double[][] reversedMatrix(double[][] mat){
		double[][] mat2 = new double[mat.length][mat[0].length];
		for(int i = 0; i < mat.length; i++){
			for(int j = 0; j < mat[0].length; j++)
				mat2[i][j] = -1 * mat[i][j];
		}
		return mat2;
	}
	public static double[][] rotatePoint(double[][] pointAsMat, double[][] rotationMat, double[][] translationMat){
		double[][] res = multiplyMat(rotationMat, translationMat, pointAsMat);
		return multiplyMat(res, reversedMatrix(translationMat), pointAsMat);
	}
	
	//--------------------------------------------------------------------
	//--------------------------Derivatives---------------------------------
	//--------------------------------------------------------------------
	
	public static double derive(Function func, double x){
		return centralDifference(func, x);
	}
	public static double derive2(Function func, double x){
		return centralDifference2(func, x);
	}
	public static double forwardDifference(Function func, double x){
		return forwardDifference(func, x, 1e-8);
	}
	public static double forwardDifference(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - func.f(x)) / changeConstant;
	}
	public static double backwardDifference(Function func, double x){
		return backwardDifference(func, x, 1e-8);
	}
	public static double backwardDifference(Function func, double x, double changeConstant){
		return (func.f(x) - func.f(x - changeConstant)) / changeConstant;
	}
	public static double centralDifference(Function func, double x){
		return centralDifference(func, x, 1e-8);
	}
	public static double centralDifference(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - func.f(x - changeConstant)) / (2 * changeConstant);
	}
	public static double centralDifference2(Function func, double x){
		return centralDifference2(func, x, 1e-8);
	}
	public static double centralDifference2(Function func, double x, double changeConstant){
		return (func.f(x + changeConstant) - 2 * func.f(x) + func.f(x - changeConstant)) / (changeConstant * changeConstant);
	}
	
	//--------------------------------------------------------------------
	//--------------------------Integrals---------------------------------
	//--------------------------------------------------------------------
	
	public static double integrate(Function func, double min, double max){
		return simpsonsRule(func, min, max);
	}
	public static double trapezoidalRule(Function func, double min, double max){
		final int DEFAULT_INTEGRAL_TREPAZOIDS = 100;
		return trapezoidalRule(func, min, max, DEFAULT_INTEGRAL_TREPAZOIDS);
	}
	public static double trapezoidalRule(Function func, double min, double max, int trapezoids){
		double h = (max - min) / trapezoids;
		double s = 0.5 * (func.f(min) + func.f(max));
		for(int i = 1; i < trapezoids; i++)
			s += func.f(min + i * h); 
		return (s * h);
	}
	public static double simpsonsRule(Function func, double min, double max){
		final int DEFAULT_INTEGRAL_SLICES = 10;
		return simpsonsRule(func, min, max, DEFAULT_INTEGRAL_SLICES);
	}
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
	
	public static double planeIntersection(Vector3 origin, Vector3 direction, 
			Vector3 surfaceVec1, Vector3 surfaceVec2, Vector3 pointOnSurface){
		Vector3 n = surfaceVec1.cross(surfaceVec2);
		double d = -pointOnSurface.dot(n);
		return planeIntersection(origin, direction, n, d);
	}
	public static double planeIntersection(Vector3 origin, Vector3 direction, Vector3 n, double d){
		return -(n.dot(origin) + d) / (n.dot(direction));
	}
	public static double VecInclination(double z, double magnitude){
		double angle = Math.toDegrees(Math.acos(z / magnitude)); 
		return (z < 0)? -angle : angle;
	}
	public static double vecAzimuth(double y, double x){
		return Math.toDegrees(Math.atan2(y, x));
	}
	public static double vecMagnitude(double x, double y, double z){
		return pythagorasTheorem(x, y, z);
	}
	public static double vecX(double magnitude, double azimuth, double inclination){
		return (magnitude * Math.sin(Math.toRadians(inclination)) * Math.cos(Math.toRadians(azimuth)));
	}
	public static double vecX(double magnitude, double azimuth){
		return (magnitude * Math.cos(Math.toRadians(azimuth)));
	}
	public static double vecY(double magnitude, double azimuth, double inclination){
		return (magnitude * Math.sin(Math.toRadians(inclination)) * Math.sin(Math.toRadians(azimuth)));
	}
	public static double vecY(double magnitude, double azimuth){
		return (magnitude * Math.sin(Math.toRadians(azimuth)));
	}
	public static double vecZ(double magnitude, double inclination){
		return (magnitude * Math.cos(Math.toRadians(inclination)));
	}
}
