package edu.flash3388.flashlib.math;

public class Vector3f {
	
	private float x;
	private float y;
	private float z;
	
	public Vector3f(){
		this(0, 0, 0);
	}
	public Vector3f(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float max() {
		return Math.max(x, Math.max(y, z));
	}
	public float length(){
		return (float) Math.sqrt(x * x + y * y + z * z);
	}
	public float inclination(){
		float angle = (float) Math.toDegrees(Math.acos(z / length())); 
		return (z < 0)? -angle : angle;
	}
	public float azimuth(){
		return (float) Math.toDegrees(Math.atan2(y, x));
	}
	public void normalize(){
		float l = length();
		this.x /= l;
		this.y /= l;
		this.z /= l;
	}
	public Vector3f normalized(){
		float l = length();
		return new Vector3f(x / l, y / l, z / l);
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public float getZ(){
		return z;
	}
	public void setX(float x){
		this.x = x;
	}
	public void setY(float y){
		this.y = y;
	}
	public void setZ(float z){
		this.z = z;
	}
	public void addX(float x){
		this.x += x;
	}
	public void addY(float y){
		this.y += y;
	}
	public void addZ(float z){
		this.z += z;
	}
	
	public Vector3f set(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	public Vector3f set(Vector3f vec){
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
		return this;
	}
	
	public Vector3f add(float x, float y, float z){
		return new Vector3f(this.x + x, this.y + y, this.z + z);
	}
	public Vector3f add(float scalar){
		return new Vector3f(this.x + scalar, this.y + scalar, this.z + scalar);
	}
	public Vector3f add(Vector3f vec){
		return new Vector3f(this.x + vec.x, this.y + vec.y, this.z + vec.z);
	}
	public Vector3f sub(float x, float y, float z){
		return new Vector3f(this.x - x, this.y - y, this.z - z);
	}
	public Vector3f sub(float scalar){
		return new Vector3f(this.x - scalar, this.y - scalar, this.z - scalar);
	}
	public Vector3f sub(Vector3f vec){
		return new Vector3f(this.x - vec.x, this.y - vec.y, this.z - vec.z);
	}
	public Vector3f multiply(float scalar){
		return new Vector3f(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	public Vector3f multiply(Vector3f vec){
		return new Vector3f(this.x * vec.x, this.y * vec.y, this.z * vec.z);
	}
	public Vector3f div(float scalar){
		return new Vector3f(this.x / scalar, this.y / scalar, this.z / scalar);
	}
	public Vector3f div(Vector3f vec){
		return new Vector3f(this.x / vec.x, this.y / vec.y, this.z / vec.z);
	}
	public Vector3f cross(Vector3f vec){
		return new Vector3f(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
	}
	public float dot(Vector3f vec){
		return x * vec.x + y * vec.y + z * vec.z;
	}
	
	public Vector3f copy(){
		return new Vector3f(x, y, z);
	}
	public boolean equals(Vector3f vec){
		return x == vec.x && y == vec.y && z == vec.z;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y+" z: "+z;
	}
	
	public static Vector3f net(Vector3f...vector3s){
		float x = 0, y = 0, z = 0;
		for(Vector3f vec : vector3s){
			x += vec.x; y += vec.y; z += vec.z;
		}
		return new Vector3f(x, y, z);
	}
	public static float angleBetween(Vector3f u, Vector3f v){
		return (float) Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	public static Vector3f polar(float magnitude, float azimuth, float inclination){
		float x = Mathf.vecX(magnitude, azimuth, inclination);
		float y = Mathf.vecY(magnitude, azimuth, inclination);
		float z = Mathf.vecZ(magnitude, inclination);
		return new Vector3f(x, y, z);
	}
}
