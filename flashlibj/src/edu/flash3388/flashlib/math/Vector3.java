package edu.flash3388.flashlib.math;

/**
 * Represents a 3-dimensional vector in Cartesian space.
 * <p>
 * In mathematics, physics, and engineering, a Euclidean vector 
 * (sometimes called a geometric[1] or spatial vector,[2] or—as here—simply a vector) 
 * is a geometric object that has magnitude (or length) and direction. 
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://www.mathsisfun.com/algebra/vectors.html">https://www.mathsisfun.com/algebra/vectors.html</a>
 */
public class Vector3 {
	
	private double x;
	private double y;
	private double z;
	
	/**
	 * Creates a new empty vector
	 */
	public Vector3(){
		this(0, 0, 0);
	}
	/**
	 * Creates a new vector with given Cartesian coordinates 
	 * 
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param z the z coordinate
	 */
	public Vector3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Returns the maximum value between the 3 dimension values
	 * @return the biggest coordinate
	 */
	public double max() {
		return Math.max(x, Math.max(y, z));
	}
	/**
	 * The length of the vector.
	 * @return the length of the vector
	 * @see Mathf#pythagorasTheorem(double, double, double)
	 */
	public double length(){
		return Math.sqrt(x * x + y * y + z * z);
	}
	/**
	 * Gets the inclination of the vector. Inclination is the angle between the vector and the z-axis.
	 * @return the inclination of the vector
	 */
	public double inclination(){
		double angle = Math.toDegrees(Math.acos(z / length())); 
		return (z < 0)? -angle : angle;
	}
	/**
	 * Gets the azimuth of the vector. Azimuth is the angle between the vector and the x-axis.
	 * @return the azimuth of the vector
	 */
	public double azimuth(){
		return Math.toDegrees(Math.atan2(y, x));
	}
	/**
	 * Normalizes the vector size. Done by dividing its dimensions by its length.
	 */
	public void normalize(){
		double l = length();
		this.x /= l;
		this.y /= l;
		this.z /= l;
	}
	/**
	 * Gets a normalized version of this vector.
	 * @return a normalized version of this vector
	 * @see Vector3#normalize()
	 */
	public Vector3 normalized(){
		double l = length();
		return new Vector3(x / l, y / l, z / l);
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
	 * Gets the z coordinate of this vector.
	 * @return the z coordinate of this vector
	 */
	public double getZ(){
		return z;
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
	 * Sets the z coordinate of this vector.
	 * @param z the new z coordinate
	 */
	public void setZ(double z){
		this.z = z;
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
	 * Adds a value to the z coordinate of this vector.
	 * @param z value to add
	 */
	public void addZ(double z){
		this.z += z;
	}
	
	/**
	 * Sets the coordinates of this vector and returns this vector.
	 * 
	 * @param x the new x coordinate
	 * @param y the new y coordinate
	 * @param z the new z coordinate
	 * @return this vector
	 */
	public Vector3 set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	/**
	 * Sets the coordinates of this vector to the coordinates of a given vector and returns this vector.
	 * 
	 * @param vec the vector to copy
	 * @return this vector
	 */
	public Vector3 set(Vector3 vec){
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}
	
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and given coordinates.
	 * 
	 * @param x x coordinate to add
	 * @param y y coordinate to add
	 * @param z z coordinate to add
	 * @return the result of the addition
	 */
	public Vector3 add(double x, double y, double z){
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and a given scalar. The scalar is added to all coordinates.
	 * 
	 * @param scalar the scalar to add
	 * @return the result of the addition
	 */
	public Vector3 add(double scalar){
		return new Vector3(this.x + scalar, this.y + scalar, this.z + scalar);
	}
	/**
	 * Returns a new vector which equals to the result of addition between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to add
	 * @return the result of the addition
	 */
	public Vector3 add(Vector3 vec){
		return new Vector3(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and given coordinates.
	 * 
	 * @param x x coordinate to subtract
	 * @param y y coordinate to subtract
	 * @param z z coordinate to subtract
	 * @return the result of the subtraction
	 */
	public Vector3 sub(double x, double y, double z){
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and a given scalar. The scalar is added to all coordinates.
	 * 
	 * @param scalar the scalar to subtract
	 * @return the result of the subtraction
	 */
	public Vector3 sub(double scalar){
		return new Vector3(this.x - scalar, this.y - scalar, this.z - scalar);
	}
	/**
	 * Returns a new vector which equals to the result of subtraction between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to subtract
	 * @return the result of the subtraction
	 */
	public Vector3 sub(Vector3 vec){
		return new Vector3(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
	/**
	 * Returns a new vector which equals to the result of multiplication between 
	 * this number and a given scalar.
	 * 
	 * @param scalar scalar to multiply by
	 * @return the result of the multiplication
	 */
	public Vector3 multiply(double scalar){
		return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	/**
	 * Returns a new vector which equals to the result of multiplication between 
	 * this number and a given vector.
	 *  
	 * @param vec vector to multiply by
	 * @return the result of the multiplication
	 */
	public Vector3 multiply(Vector3 vec){
		return new Vector3(this.x * vec.x, this.y * vec.y, this.z * vec.z);
	}
	/**
	 * Returns a new vector which equals to the result of division between 
	 * this number and a given scalar.
	 * 
	 * @param scalar scalar to divide by
	 * @return the result of the division
	 */
	public Vector3 div(double scalar){
		return new Vector3(this.x / scalar, this.y / scalar, this.z / scalar);
	}
	/**
	 * Returns a new vector which equals to the result of division between 
	 * this number and a given vector. The division is done by dividing between the
	 * respective coordinates of the vector.
	 *  
	 * @param vec vector to divide by
	 * @return the result of the division
	 */
	public Vector3 div(Vector3 vec){
		return new Vector3(this.x / vec.x, this.y / vec.y, this.z / vec.z);
	}
	
	/**
	 * Returns the result of cross product between this vector and a given vector.
	 * 
	 * @param vec a vector to perform a cross product by
	 * @return the result of the cross product
	 */
	public Vector3 cross(Vector3 vec){
		return new Vector3(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}
	/**
	 * Returns the result of dot product between this vector and a given vector.
	 * 
	 * @param vec a vector to perform a dot product by
	 * @return the result of the dot product
	 */
	public double dot(Vector3 vec){
		return x * vec.x + y * vec.y + z * vec.z;
	}
	
	/**
	 * Returns a copy of this vector.
	 * @return a copy of this vector
	 */
	public Vector3 copy(){
		return new Vector3(x, y, z);
	}
	/**
	 * Gets whether or not this vector equals to another. Compares their coordinates.
	 * @param vec vector to check against
	 * @return true if the vectors are the same
	 */
	public boolean equals(Vector3 vec){
		return x == vec.x && y == vec.y && z == vec.z;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y+" z: "+z;
	}
	
	/**
	 * Returns the net vector of the given vector array. The result is the addition between
	 * the coordinates of each vector.
	 * 
	 * @param vector3s the vector array
	 * @return the net vector
	 */
	public static Vector3 net(Vector3...vector3s){
		double x = 0, y = 0, z = 0;
		for(Vector3 vec : vector3s){
			x += vec.x; y += vec.y; z += vec.z;
		}
		return new Vector3(x, y, z);
	}
	/**
	 * Returns the angle between two vectors.
	 * 
	 * @param u vector 1
	 * @param v vector 2
	 * @return the angle in degrees between the vectors
	 */
	public static double angleBetween(Vector3 u, Vector3 v){
		return Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	/**
	 * Creates a new vector from polar coordinates. The coordinates are converted into Cartesian coordinates.
	 * 
	 * @param magnitude the length of the vector
	 * @param azimuth the azimuth of the vector
	 * @param inclination the inclination of the vector
	 * @return a new vector
	 */
	public static Vector3 polar(double magnitude, double azimuth, double inclination){
		double x = Mathf.vecX(magnitude, azimuth, inclination);
		double y = Mathf.vecY(magnitude, azimuth, inclination);
		double z = Mathf.vecZ(magnitude, inclination);
		return new Vector3(x, y, z);
	}
}
