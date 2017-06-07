package edu.flash3388.flashlib.math;

public class Vector2f {

	private float x;
	private float y;
	
	public Vector2f(){
		this(0, 0);
	}
	public Vector2f(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float length(){
		return (float) Math.sqrt(x * x + y * y);
	}
	public float angle(){
		return (float) Math.toDegrees(Math.atan2(y, y));
	}
	public void normalize(){
		float l = length();
		this.x /= l;
		this.y /= l;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public void setX(float x){
		this.x = x;
	}
	public void setY(float y){
		this.y = y;
	}
	public void addX(float x){
		this.x += x;
	}
	public void addY(float y){
		this.y += y;
	}
	
	public void set(float x, float y){
		this.x = x;
		this.y = y;
	}
	public void set(Vector2f vec){
		this.x = vec.x;
		this.y = vec.y;
	}
	
	public Vector2f add(float x, float y){
		return new Vector2f(this.x + x, this.y + y);
	}
	public Vector2f add(float scalar){
		return new Vector2f(this.x + scalar, this.y + scalar);
	}
	public Vector2f add(Vector2f vec){
		return new Vector2f(this.x + vec.x, this.y + vec.y);
	}
	public Vector2f sub(float x, float y){
		return new Vector2f(this.x - x, this.y - y);
	}
	public Vector2f sub(float scalar){
		return new Vector2f(this.x - scalar, this.y - scalar);
	}
	public Vector2f sub(Vector2f vec){
		return new Vector2f(this.x - vec.x, this.y - vec.y);
	}
	public Vector2f multiply(float scalar){
		return new Vector2f(this.x * scalar, this.y * scalar);
	}
	public Vector2f multiply(Vector2f vec){
		return new Vector2f(this.x * vec.x, this.y * vec.y);
	}
	public Vector2f div(float scalar){
		return new Vector2f(this.x / scalar, this.y / scalar);
	}
	public Vector2f div(Vector2f vec){
		return new Vector2f(this.x / vec.x, this.y / vec.y);
	}
	public float cross(Vector2f vec){
		return x * vec.x - y * vec.y;
	}
	public float dot(Vector2f vec){
		return x * vec.x + y * vec.y;
	}
	
	public void rotate(float angle){
		Vector2f vec = rotate2(angle);
		x = vec.x;
		y = vec.y;
	}
	public Vector2f rotate2(float angle){
		float rad = (float) Math.toRadians(angle);
		float cos = (float) Math.cos(rad);
		float sin = (float) Math.sin(rad);
		
		return new Vector2f((x * cos - y * sin),(x * sin + y * cos));
	}
	
	public boolean equals(Vector2f vec){
		return x == vec.x && y == vec.y;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y;
	}
	
	public static Vector2f net(Vector2f...vector2s){
		float x = 0, y = 0;
		for(Vector2f vec : vector2s){
			x += vec.x; y += vec.y;
		}
		return new Vector2f(x, y);
	}
	public static float angleBetween(Vector2f u, Vector2f v){
		return (float) Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	public static Vector2f polar(float magnitude, float azimuth){
		float x = Mathf.vecX(magnitude, azimuth);
		float y = Mathf.vecY(magnitude, azimuth);
		return new Vector2f(x, y);
	}
}
