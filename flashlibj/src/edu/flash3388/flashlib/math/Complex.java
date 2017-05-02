package edu.flash3388.flashlib.math;

public class Complex {
	
	private double real;
	private double imaginary;
	
	public Complex(double real, double imaginary){
		set(real, imaginary);
	}
	public Complex(Complex com){
		set(com);
	}
	public Complex(){
		set(0, 0);
	}
	
	public double real(){
		return real;
	}
	public double imaginary(){
		return imaginary;
	}
	public double length(){
		return Mathd.pythagorasTheorem(real, imaginary);
	}
	public double angle(){
		return Math.atan2(imaginary, real);
	}
	public double angleDegrees(){
		return Math.toDegrees(angle());
	}
	
	public Complex conjugate(){
		return new Complex(real, -imaginary);
	}
	public Complex[] roots(int degree){
		Complex[] roots = new Complex[degree];
		double l = Mathd.root(length(), degree), angle = angle();
		for(int i = 0; i < degree; i++)
			roots[i] = euler(l, (angle + 2 * Math.PI * i) / degree);
		return roots;
	}
	
	public void set(double real, double imaginary){
		this.real = real;
		this.imaginary = imaginary;
	}
	public void set(Complex com){
		this.real = com.real;
		this.imaginary = com.imaginary;
	}
	public void setReal(double real){
		this.real = real;
	}
	public void setImaginary(double imaginary){
		this.imaginary = imaginary;
	}
	
	public Complex add(double real, double imaginary){
		return new Complex(this.real + real, this.imaginary + imaginary);
	}
	public Complex add(Complex com){
		return new Complex(this.real + com.real, this.imaginary + com.imaginary);
	}
	public Complex sub(double real, double imaginary){
		return new Complex(this.real - real, this.imaginary - imaginary);
	}
	public Complex sub(Complex com){
		return new Complex(this.real - com.real, this.imaginary - com.imaginary);
	}
	public Complex multiply(double scalar){
		return new Complex(this.real * scalar, this.imaginary * scalar);
	}
	public Complex multiply(Complex com){
		return new Complex(real * com.real - imaginary * com.imaginary, 
				real * com.imaginary + imaginary * com.real);
	}
	public Complex divide(double scalar){
		return new Complex(this.real / scalar, this.imaginary / scalar);
	}
	public Complex divide(Complex com){
		if(com.imaginary == 0) return divide(com.real);
		Complex conj = com.conjugate();
		return multiply(conj).divide(com.multiply(conj));
	}
	
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
	
	public static Complex euler(double length, double anglerad){
		return new Complex(length * Math.cos(anglerad), length * Math.sin(anglerad));
	}
}
