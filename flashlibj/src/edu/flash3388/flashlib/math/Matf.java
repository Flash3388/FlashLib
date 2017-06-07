package edu.flash3388.flashlib.math;

public class Matf {
private float[][] mat;
	
	public Matf(int rows, int coloumns){
		mat = new float[rows][coloumns];
	}
	public Matf(float[][] mat){
		this.mat = mat;
	}
	
	public int rows(){
		return mat.length;
	}
	public int coloumns(){
		if(rows() > 0) return mat[0].length;
		return 0;
	}
	public float max(){
		float max = mat[0][0];
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++){
				if(mat[i][j] > max)
					max = mat[i][j];
			}
		}
		return max;
	}
	
	public float[][] get(){
		return mat;
	}
	public float get(int row, int coloumn){
		return mat[row][coloumn];
	}
	public float[] getRow(int row){
		return mat[row];
	}
	public float[] getColoumn(int coloumn){
		float[] col = new float[rows()];
		for(int i = 0; i < col.length; i++)
			col[i] = mat[i][coloumn];
		return col;
	}
	
	public void set(float[][] mat){
		this.mat = mat;
	}
	public void set(int row, int coloumn, float val){
		mat[row][coloumn] = val;
	}
	public void setRow(int row, float[] val){
		mat[row] = val;
	}
	public void setColoumn(int coloumn, float[] val){
		for(int i = 0; i < val.length; i++)
			mat[i][coloumn] = val[i];
	}
	
	public void reverse(){
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++)
				mat[i][j] *= -1;
		}
	}
	public void reset(){
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++)
				mat[i][j] = 0.0f;
		}
	}
	
	public Matf multiply(Matf matrix){
		if(coloumns() != matrix.rows())
			throw new IllegalArgumentException("Cannot multiply! coloumns do not equal to rows");
		return new Matf(Mathf.multiplyMat(mat, matrix.mat));
	}
	
	public Vector3f transform(Vector3f r){
		if(rows() < 4 && coloumns() < 4)
			throw new IllegalStateException("Matrix cannot create transform. rows() < 4 coloumns() < 4");
		return new Vector3f(
				mat[0][0] * r.getX() + mat[0][1] * r.getY() + mat[0][2] * r.getZ() + mat[0][3],
				mat[1][0] * r.getX() + mat[1][1] * r.getY() + mat[1][2] * r.getZ() + mat[1][3],
				mat[2][0] * r.getX() + mat[2][1] * r.getY() + mat[2][2] * r.getZ() + mat[2][3]);
	}
	
	public Vector3f toVector3f(){
		if(rows() < 3 && coloumns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 coloumns() < 1");
		return new Vector3f(mat[0][0], mat[1][0], mat[2][0]);
	}
	public Vector2f toVector2f(){
		if(rows() < 2 && coloumns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 coloumns() < 1");
		return new Vector2f(mat[0][0], mat[1][0]);
	}
	
	public Vector3f fill(Vector3f vector){
		if(rows() < 3 && coloumns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 coloumns() < 1");
		vector.set(mat[0][0], mat[1][0], mat[2][0]);
		return vector;
	}
	public Vector2f fill(Vector2f vector){
		if(rows() != 2 && coloumns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 coloumns() < 1");
		vector.set(mat[0][0], mat[1][0]);
		return vector;
	}
	
	public boolean equals(Matf mat){
		return equals(mat.mat);
	}
	public boolean equals(float[][] mat2){
		if(rows() != mat2.length || coloumns() != mat2[0].length) return false;
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++)
				if(mat[i][j] != mat2[i][j])
					return false;
		}
		return true;
	}
	public Matf copy(){
		return new Matf(copy2());
	}
	public float[][] copy2(){
		float[][] m = new float[rows()][coloumns()];
		for(int i = 0; i < m.length; i++){
			for(int j = 0; j < m[0].length; j++)
				m[i][j] = mat[i][j];
		}
		return m;
	}
	
	public static Matf initRotation3(Vector3f forward, Vector3f up){
		Vector3f f = forward.normalized();
		Vector3f r = up.normalized();
		
		r = r.cross(f);
		Vector3f u = f.cross(r);

		return initRotation3(f, u, r);
	}
	public static Matf initRotation3(Vector3f f, Vector3f u, Vector3f r){
		float[][] m = new float[4][4];
		m[0][0] = r.getX();	m[0][1] = r.getY();	m[0][2] = r.getZ();	m[0][3] = 0;
		m[1][0] = u.getX();	m[1][1] = u.getY();	m[1][2] = u.getZ();	m[1][3] = 0;
		m[2][0] = f.getX();	m[2][1] = f.getY();	m[2][2] = f.getZ();	m[2][3] = 0;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
		return new Matf(m);
	}
	public static Matf initRotation3(float x, float y, float z){
		return new Matf(Mathf.rotationMatrix3d(x, y, z));
	}
}
