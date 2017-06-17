package edu.flash3388.flashlib.math;

/**
 * Represents a complex number. 
 * <p>
 * A complex number is a number that can be expressed in the form of z = a + bi. Where a and
 * b are real numbers and i is an imaginary number that satisfies the equation: pow(i, 2) = -1.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Complex_number">https://en.wikipedia.org/wiki/Complex_number</a>
 */
public class Complex {
	
	private double real;
	private double imaginary;
	
	/**
	 * Creates a complex number with given real and imaginary values.
	 * 
	 * @param real the real value
	 * @param imaginary the imaginary value
	 */
	public Complex(double real, double imaginary){
		set(real, imaginary);
	}
	/**
	 * Creates a copy of a given complex number .
	 * 
	 * @param com the complex number
	 */
	public Complex(Complex com){
		set(com);
	}
	/**
	 * Creates an "empty" complex number with (0,0).
	 */
	public Complex(){
		set(0, 0);
	}
	
	/**
	 * Gets the real value of the complex number
	 * @return the real part
	 */
	public double real(){
		return real;
	}
	/**
	 * Gets the imaginary value of the complex number
	 * @return the imaginary part
	 */
	public double imaginary(){
		return imaginary;
	}
	/**
	 * Gets the length of the complex number in a polar coordinate system.
	 * @return the length of the complex number
	 * @see Mathf#pythagorasTheorem(double, double)
	 */
	public double length(){
		return Mathf.pythagorasTheorem(real, imaginary);
	}
	/**
	 * Gets the angle in radians of the complex number in a polar coordinate system.
	 * @return the angle of the complex number
	 * @see Math#atan2(double, double)
	 */
	public double angle(){
		return Math.atan2(imaginary, real);
	}
	/**
	 * Gets the angle in degrees of the complex number in a polar coordinate system.
	 * @return the angle of the complex number
	 * @see Math#atan2(double, double)
	 */
	public double angleDegrees(){
		return Math.toDegrees(angle());
	}
	
	/**
	 * Gets the conjugated number of this complex number:<br>
	 * conjugate = (real, -imaginary)
	 * @return the conjugate 
	 */
	public Complex conjugate(){
		return new Complex(real, -imaginary);
	}
	
	/**
	 * Gets the roots of a degree for this number.
	 * 
	 * @param degree the degree of the root
	 * @return the roots of this number
	 */
	public Complex[] roots(int degree){
		Complex[] roots = new Complex[degree];
		double l = Mathf.root(length(), degree), angle = angle();
		for(int i = 0; i < degree; i++)
			roots[i] = euler(l, (angle + 2 * Math.PI * i) / degree);
		return roots;
	}
	
	/**
	 * Sets the value of the complex number parts.
	 * 
	 * @param real the real part
	 * @param imaginary the imaginary part
	 */
	public void set(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	/**
	 * Sets the value of the complex number parts to the parts of a given complex number.
	 * @param com the complex number
	 */
	public void set(Complex com){
		this.real = com.real;
		this.imaginary = com.imaginary;
	}
	
	/**
	 * Sets the real part of this complex number.
	 * @param real the real value
	 */
	public void setReal(double real){
		this.real = real;
	}
	/**
	 * Sets the imaginary part of this complex number.
	 * @param imaginary the imaginary value
	 */
	public void setImaginary(double imaginary){
		this.imaginary = imaginary;
	}
	
	/**
	 * Returns a new complex number which equals to the result of addition between 
	 * this number and given real and imaginary components.
	 * 
	 * @param real the real value
	 * @param imaginary the imaginary value
	 * @return the result of the addition
	 */
	public Complex add(double real, double imaginary){
		return new Complex(this.real + real, this.imaginary + imaginary);
	}
	/**
	 * Returns a new complex number which equals to the result of addition between 
	 * this number and given complex number.
	 * 
	 * @param com the complex number
	 * @return the result of the addition
	 */
	public Complex add(Complex com){
		return new Complex(this.real + com.real, this.imaginary + com.imaginary);
	}
	/**
	 * Returns a new complex number which equals to the result of subtraction between 
	 * this number and given real and imaginary components.
	 * 
	 * @param real the real value
	 * @param imaginary the imaginary value
	 * @return the result of the subtraction
	 */
	public Complex sub(double real, double imaginary){
		return new Complex(this.real - real, this.imaginary - imaginary);
	}
	/**
	 * Returns a new complex number which equals to the result of subtraction between 
	 * this number and given complex number.
	 * 
	 * @param com the complex number
	 * @return the result of the subtraction
	 */
	public Complex sub(Complex com){
		return new Complex(this.real - com.real, this.imaginary - com.imaginary);
	}
	/**
	 * Returns a new complex number which equals to the result of multiplication between 
	 * this number and a given scalar.
	 * 
	 * @param scalar a scalar value
	 * @return the result of the multiplication
	 */
	public Complex multiply(double scalar){
		return new Complex(this.real * scalar, this.imaginary * scalar);
	}
	/**
	 * Returns a new complex number which equals to the result of multiplication between 
	 * this number and a given complex number.
	 * 
	 * @param com a complex number
	 * @return the result of the multiplication
	 */
	public Complex multiply(Complex com){
		return new Complex(real * com.real - imaginary * com.imaginary, 
				real * com.imaginary + imaginary * com.real);
	}
	/**
	 * Returns a new complex number which equals to the result of division between 
	 * this number and a given scalar.
	 * 
	 * @param scalar a scalar value
	 * @return the result of the division
	 */
	public Complex divide(double scalar){
		return new Complex(this.real / scalar, this.imaginary / scalar);
	}
	/**
	 * Returns a new complex number which equals to the result of division between 
	 * this number and a given complex number. This is done by multiplying this by the 
	 * conjugate value of the given number, and dividing the result by the multiplication 
	 * of the given number by its conjugate.
	 * 
	 * @param com a complex number
	 * @return the result of the division
	 */
	public Complex divide(Complex com){
		if(com.imaginary == 0) return divide(com.real);
		Complex conj = com.conjugate();
		return multiply(conj).divide(com.multiply(conj));
	}
	
	/**
	 * Gets whether or not this complex equals to another. Compares their coordinates.
	 * @param com complex to check against
	 * @return true if the complex numbers are the same
	 */
	public boolean equals(Complex com){
		return com.real == real && com.imaginary == imaginary;
	}
	@Override
	public String toString(){
		String str = "";
		if(real != 0) str += String.valueOf(real);
		if(imaginary != 0) 
			str += ((real != 0)? ((imaginary > 0)? " + " : " - ") + Math.abs(imaginary) : imaginary) + "i";
		return str;
	}
	
	/**
	 * Creates a new complex number from a given eular format values: length and angle.
	 * 
	 * @param length the length of the complex number
	 * @param anglerad the angle of the complex number in radians
	 * @return a new complex number
	 */
	public static Complex euler(double length, double anglerad){
		return new Complex(length * Math.cos(anglerad), length * Math.sin(anglerad));
	}
}
