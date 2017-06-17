package edu.flash3388.flashlib.math;

/**
 * Represents a 2-dimensional vector in Cartesian space.
 * <p>
 * In mathematics, physics, and engineering, a Euclidean vector 
 * (sometimes called a geometric[1] or spatial vector,[2] or—as here—simply a vector) 
 * is a geometric object that has magnitude (or length) and direction. 
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://www.mathsisfun.com/algebra/vectors.html">https://www.mathsisfun.com/algebra/vectors.html</a>
 */
public class Vector2 {

	private double x;
	private double y;
	
	/**
	 * Creates a new empty vector
	 */
	public Vector2(){
		this(0, 0);
	}
	/**
	 * Creates a new vector with given Cartesian coordinates 
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public Vector2(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	/**
	 * The length of the vector.
	 * @return the length of the vector
	 * @see Mathf#pythagorasTheorem(double, double)
	 */
	public double length(){
		return Math.sqrt(x * x + y * y);
	}
	/**
	 * Gets the angle of the vector. Angle is the angle between the vector and the x-axis.
	 * @return the angle of the vector
	 */
	public double angle(){
		return Math.toDegrees(Math.atan2(y, y));
	}
	/**
	 * Normalizes the vector size. Done by dividing its dimensions by its length.
	 */
	public void normalize(){
		double l = length();
		this.x /= l;
		this.y /= l;
	}
	
	/**
	 * Gets the x coordinate of this vector.
	 * @return the x coordinate of this vector
	 */
	public double getX(){
		return x;
	}
	/**
	 * Gets the y coordinate of this vector.
	 * @return the y coordinate of this vector
	 */
	public double getY(){
		return y;
	}
	
	/**
	 * Sets the x coordinate of this vector.
	 * @param x the new x coordinate
	 */
	public void setX(double x){
		this.x = x;
	}
	/**
	 * Sets the y coordinate of this vector.
	 * @param y the new y coordinate
	 */
	public void setY(double y){
		this.y = y;
	}
	
	/**
	 * Adds a value to the x coordinate of this vector.
	 * @param x value to add
	 */
	public void addX(double x){
		this.x += x;
	}
	/**
	 * Adds a value to the y coordinate of this vector.
	 * @param y value to add
	 */
	public void addY(double y){
		this.y += y;
	}
	
	/**
	 * Sets the coordinates of this vector and returns this vector.
	 * 
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 * @return this vector
	 */
	public void set(double x, double y){
		this.x = x;
		this.y = y;
	}
	/**
	 * Sets the coordinates of this vector to the coordinates of a given vector and returns this vector.
	 * 
	 * @param vec the vector to copy
	 * @return this vector
	 */
	public void set(Vector2 vec){
		this.x = vec.x;
		this.y = vec.y;
	}
	
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and given coordinates.
	 * 
	 * @param x x coordinate to add
	 * @param y y coordinate to add
	 * @return the result of the addition
	 */
	public Vector2 add(double x, double y){
		return new Vector2(this.x + x, this.y + y);
	}
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and a given scalar. The scalar is added to all coordinates.
	 * 
	 * @param scalar the scalar to add
	 * @return the result of the addition
	 */
	public Vector2 add(double scalar){
		return new Vector2(this.x + scalar, this.y + scalar);
	}
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to add
	 * @return the result of the addition
	 */
	public Vector2 add(Vector2 vec){
		return new Vector2(this.x + vec.x, this.y + vec.y);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and given coordinates.
	 * 
	 * @param x x coordinate to subtract
	 * @param y y coordinate to subtract
	 * @return the result of the subtraction
	 */
	public Vector2 sub(double x, double y){
		return new Vector2(this.x - x, this.y - y);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and a given scalar. The scalar is added to all coordinates.
	 * 
	 * @param scalar the scalar to subtract
	 * @return the result of the subtraction
	 */
	public Vector2 sub(double scalar){
		return new Vector2(this.x - scalar, this.y - scalar);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to subtract
	 * @return the result of the subtraction
	 */
	public Vector2 sub(Vector2 vec){
		return new Vector2(this.x - vec.x, this.y - vec.y);
	}
	/**
	 * Returns a new vector which equals to the result of multiplication between 
	 * this number and a given scalar.
	 * 
	 * @param scalar scalar to multiply by
	 * @return the result of the multiplication
	 */
	public Vector2 multiply(double scalar){
		return new Vector2(this.x * scalar, this.y * scalar);
	}
	/**
	 * Returns a new vector which equals to the result of multiplication between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to multiply by
	 * @return the result of the multiplication
	 */
	public Vector2 multiply(Vector2 vec){
		return new Vector2(this.x * vec.x, this.y * vec.y);
	}
	/**
	 * Returns a new vector which equals to the result of division between 
	 * this number and a given scalar.
	 * 
	 * @param scalar scalar to divide by
	 * @return the result of the division
	 */
	public Vector2 div(double scalar){
		return new Vector2(this.x / scalar, this.y / scalar);
	}
	/**
	 * Returns a new vector which equals to the result of division between 
	 * this number and a given vector. The division is done by dividing between the
	 * respective coordinates of the vector.
	 *  
	 * @param vec vector to divide by
	 * @return the result of the division
	 */
	public Vector2 div(Vector2 vec){
		return new Vector2(this.x / vec.x, this.y / vec.y);
	}
	/**
	 * Returns the result of cross product between this vector and a given vector.
	 * 
	 * @param vec a vector to perform a cross product by
	 * @return the result of the cross product
	 */
	public double cross(Vector2 vec){
		return x * vec.x - y * vec.y;
	}
	/**
	 * Returns the result of dot product between this vector and a given vector.
	 * 
	 * @param vec a vector to perform a dot product by
	 * @return the result of the dot product
	 */
	public double dot(Vector2 vec){
		return x * vec.x + y * vec.y;
	}
	
	/**
	 * Gets whether or not this vector equals to another. Compares their coordinates.
	 * @param vec vector to check against
	 * @return true if the vectors are the same
	 */
	public boolean equals(Vector2 vec){
		return x == vec.x && y == vec.y;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y;
	}
	
	/**
	 * Returns the net vector of the given vector array. The result is the addition between
	 * the coordinates of each vector.
	 * 
	 * @param vector2s the vector array
	 * @return the net vector
	 */
	public static Vector2 net(Vector2...vector2s){
		double x = 0, y = 0;
		for(Vector2 vec : vector2s){
			x += vec.x; y += vec.y;
		}
		return new Vector2(x, y);
	}
	/**
	 * Returns the angle between two vectors.
	 * 
	 * @param u vector 1
	 * @param v vector 2
	 * @return the angle in degrees between the vectors
	 */
	public static double angleBetween(Vector2 u, Vector2 v){
		return Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	/**
	 * Creates a new vector from polar coordinates. The coordinates are converted into Cartesian coordinates.
	 * 
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @return a new vector
	 */
	public static Vector2 polar(double magnitude, double azimuth){
		double x = Mathf.vecX(magnitude, azimuth);
		double y = Mathf.vecY(magnitude, azimuth);
		return new Vector2(x, y);
	}
}
