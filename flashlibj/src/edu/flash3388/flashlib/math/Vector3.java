package edu.flash3388.flashlib.math;

public class Vector3 {
	private double x;
	private double y;
	private double z;
	
	public Vector3(){
		this(0, 0, 0);
	}
	public Vector3(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double max() {
		return Math.max(x, Math.max(y, z));
	}
	public double length(){
		return Math.sqrt(x * x + y * y + z * z);
	}
	public double inclination(){
		double angle = Math.toDegrees(Math.acos(z / length())); 
		return (z < 0)? -angle : angle;
	}
	public double azimuth(){
		return Math.toDegrees(Math.atan2(y, x));
	}
	public void normalize(){
		double l = length();
		this.x /= l;
		this.y /= l;
		this.z /= l;
	}
	public Vector3 normalized(){
		double l = length();
		return new Vector3(x / l, y / l, z / l);
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public double getZ(){
		return z;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void setZ(double z){
		this.z = z;
	}
	public void addX(double x){
		this.x += x;
	}
	public void addY(double y){
		this.y += y;
	}
	public void addZ(double z){
		this.z += z;
	}
	
	public Vector3 set(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public Vector3 set(Vector3 vec){
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}
	
	public Vector3 add(double x, double y, double z){
		return new Vector3(this.x + x, this.y + y, this.z + z);
	}
	public Vector3 add(double scalar){
		return new Vector3(this.x + scalar, this.y + scalar, this.z + scalar);
	}
	public Vector3 add(Vector3 vec){
		return new Vector3(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
	public Vector3 sub(double x, double y, double z){
		return new Vector3(this.x - x, this.y - y, this.z - z);
	}
	public Vector3 sub(double scalar){
		return new Vector3(this.x - scalar, this.y - scalar, this.z - scalar);
	}
	public Vector3 sub(Vector3 vec){
		return new Vector3(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
	public Vector3 multiply(double scalar){
		return new Vector3(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	public Vector3 multiply(Vector3 vec){
		return new Vector3(this.x * vec.x, this.y * vec.y, this.z * vec.z);
	}
	public Vector3 div(double scalar){
		return new Vector3(this.x / scalar, this.y / scalar, this.z / scalar);
	}
	public Vector3 div(Vector3 vec){
		return new Vector3(this.x / vec.x, this.y / vec.y, this.z / vec.z);
	}
	public Vector3 cross(Vector3 vec){
		return new Vector3(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}
	public double dot(Vector3 vec){
		return x * vec.x + y * vec.y + z * vec.z;
	}
	
	public Vector3 copy(){
		return new Vector3(x, y, z);
	}
	public boolean equals(Vector3 vec){
		return x == vec.x && y == vec.y && z == vec.z;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y+" z: "+z;
	}
	
	public static Vector3 net(Vector3...vector3s){
		double x = 0, y = 0, z = 0;
		for(Vector3 vec : vector3s){
			x += vec.x; y += vec.y; z += vec.z;
		}
		return new Vector3(x, y, z);
	}
	public static double angleBetween(Vector3 u, Vector3 v){
		return Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	public static Vector3 polar(double magnitude, double azimuth, double inclination){
		double x = Mathd.getX(magnitude, azimuth, inclination);
		double y = Mathd.getY(magnitude, azimuth, inclination);
		double z = Mathd.getZ(magnitude, inclination);
		return new Vector3(x, y, z);
	}
}
