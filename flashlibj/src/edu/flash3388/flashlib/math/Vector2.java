package edu.flash3388.flashlib.math;

public class Vector2 {

	private double x;
	private double y;
	
	public Vector2(){
		this(0, 0);
	}
	public Vector2(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public double length(){
		return Math.sqrt(x * x + y * y);
	}
	public double angle(){
		return Math.toDegrees(Math.atan2(y, y));
	}
	public void normalize(){
		double l = length();
		this.x /= l;
		this.y /= l;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
	public void setX(double x){
		this.x = x;
	}
	public void setY(double y){
		this.y = y;
	}
	public void addX(double x){
		this.x += x;
	}
	public void addY(double y){
		this.y += y;
	}
	
	public void set(double x, double y){
		this.x = x;
		this.y = y;
	}
	public void set(Vector2 vec){
		this.x = vec.x;
		this.y = vec.y;
	}
	
	public Vector2 add(double x, double y){
		return new Vector2(this.x + x, this.y + y);
	}
	public Vector2 add(double scalar){
		return new Vector2(this.x + scalar, this.y + scalar);
	}
	public Vector2 add(Vector2 vec){
		return new Vector2(this.x + vec.x, this.y + vec.y);
	}
	public Vector2 sub(double x, double y){
		return new Vector2(this.x - x, this.y - y);
	}
	public Vector2 sub(double scalar){
		return new Vector2(this.x - scalar, this.y - scalar);
	}
	public Vector2 sub(Vector2 vec){
		return new Vector2(this.x - vec.x, this.y - vec.y);
	}
	public Vector2 multiply(double scalar){
		return new Vector2(this.x * scalar, this.y * scalar);
	}
	public Vector2 multiply(Vector2 vec){
		return new Vector2(this.x * vec.x, this.y * vec.y);
	}
	public Vector2 div(double scalar){
		return new Vector2(this.x / scalar, this.y / scalar);
	}
	public Vector2 div(Vector2 vec){
		return new Vector2(this.x / vec.x, this.y / vec.y);
	}
	public double cross(Vector2 vec){
		return x * vec.x - y * vec.y;
	}
	public double dot(Vector2 vec){
		return x * vec.x + y * vec.y;
	}
	
	public void rotate(double angle){
		Vector2 vec = rotate2(angle);
		x = vec.x;
		y = vec.y;
	}
	public Vector2 rotate2(double angle){
		double rad = Math.toRadians(angle);
		double cos = Math.cos(rad);
		double sin = Math.sin(rad);
		
		return new Vector2((x * cos - y * sin),(x * sin + y * cos));
	}
	
	public boolean equals(Vector2 vec){
		return x == vec.x && y == vec.y;
	}
	@Override
	public String toString(){
		return "x: "+x+" y: "+y;
	}
	
	public static Vector2 net(Vector2...vector2s){
		double x = 0, y = 0;
		for(Vector2 vec : vector2s){
			x += vec.x; y += vec.y;
		}
		return new Vector2(x, y);
	}
	public static double angleBetween(Vector2 u, Vector2 v){
		return Math.toDegrees(Math.acos(u.dot(v) / (u.length() + v.length())));
	}
	public static Vector2 polar(double magnitude, double azimuth){
		double x = Mathd.getX(magnitude, azimuth);
		double y = Mathd.getY(magnitude, azimuth);
		return new Vector2(x, y);
	}
}
