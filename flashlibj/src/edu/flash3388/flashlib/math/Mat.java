package edu.flash3388.flashlib.math;

public class Mat {

	private double[][] mat;
	
	public Mat(int rows, int coloumns){
		mat = new double[rows][coloumns];
	}
	public Mat(double[][] mat){
		this.mat = mat;
	}
	
	public int rows(){
		return mat.length;
	}
	public int coloumns(){
		if(rows() > 0) return mat[0].length;
		return 0;
	}
	public double max(){
		double max = mat[0][0];
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++){
				if(mat[i][j] > max)
					max = mat[i][j];
			}
		}
		return max;
	}
	
	public double[][] get(){
		return mat;
	}
	public double get(int row, int coloumn){
		return mat[row][coloumn];
	}
	public double[] getRow(int row){
		return mat[row];
	}
	public double[] getColoumn(int coloumn){
		double[] col = new double[rows()];
		for(int i = 0; i < col.length; i++)
			col[i] = mat[i][coloumn];
		return col;
	}
	
	public void set(double[][] mat){
		this.mat = mat;
	}
	public void set(int row, int coloumn, double val){
		mat[row][coloumn] = val;
	}
	public void setRow(int row, double[] val){
		mat[row] = val;
	}
	public void setColoumn(int coloumn, double[] val){
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
	
	public Mat multiply(Mat matrix){
		if(coloumns() != matrix.rows())
			throw new IllegalArgumentException("Cannot multiply! coloumns do not equal to rows");
		return new Mat(Mathd.multiplyMat(mat, matrix.mat));
	}
	
	public Vector3 transform(Vector3 r){
		if(rows() < 4 && coloumns() < 4)
			throw new IllegalStateException("Matrix cannot create transform. rows() < 4 coloumns() < 4");
		return new Vector3(
				mat[0][0] * r.getX() + mat[0][1] * r.getY() + mat[0][2] * r.getZ() + mat[0][3],
				mat[1][0] * r.getX() + mat[1][1] * r.getY() + mat[1][2] * r.getZ() + mat[1][3],
				mat[2][0] * r.getX() + mat[2][1] * r.getY() + mat[2][2] * r.getZ() + mat[2][3]);
	}
	
	public Vector3 toVector3(){
		if(rows() < 3 && coloumns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 coloumns() < 1");
		return new Vector3(mat[0][0], mat[1][0], mat[2][0]);
	}
	public Vector2 toVector2(){
		if(rows() < 2 && coloumns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 coloumns() < 1");
		return new Vector2(mat[0][0], mat[1][0]);
	}
	
	public Vector3 fill(Vector3 vector){
		if(rows() < 3 && coloumns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 coloumns() < 1");
		vector.set(mat[0][0], mat[1][0], mat[2][0]);
		return vector;
	}
	public Vector2 fill(Vector2 vector){
		if(rows() != 2 && coloumns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 coloumns() < 1");
		vector.set(mat[0][0], mat[1][0]);
		return vector;
	}
	
	public boolean equals(Mat mat){
		return equals(mat.mat);
	}
	public boolean equals(double[][] mat2){
		if(rows() != mat2.length || coloumns() != mat2[0].length) return false;
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < coloumns(); j++)
				if(mat[i][j] != mat2[i][j])
					return false;
		}
		return true;
	}
	public Mat copy(){
		return new Mat(copy2());
	}
	public double[][] copy2(){
		double[][] m = new double[rows()][coloumns()];
		for(int i = 0; i < m.length; i++){
			for(int j = 0; j < m[0].length; j++)
				m[i][j] = mat[i][j];
		}
		return m;
	}
	
	public static Mat initRotation3(Vector3 forward, Vector3 up){
		Vector3 f = forward.normalized();
		Vector3 r = up.normalized();
		
		r = r.cross(f);
		Vector3 u = f.cross(r);

		return initRotation3(f, u, r);
	}
	public static Mat initRotation3(Vector3 f, Vector3 u, Vector3 r){
		double[][] m = new double[4][4];
		m[0][0] = r.getX();	m[0][1] = r.getY();	m[0][2] = r.getZ();	m[0][3] = 0;
		m[1][0] = u.getX();	m[1][1] = u.getY();	m[1][2] = u.getZ();	m[1][3] = 0;
		m[2][0] = f.getX();	m[2][1] = f.getY();	m[2][2] = f.getZ();	m[2][3] = 0;
		m[3][0] = 0;		m[3][1] = 0;		m[3][2] = 0;		m[3][3] = 1;
		return new Mat(m);
	}
	public static Mat initRotation3(double x, double y, double z){
		return new Mat(Mathd.rotationMatrix3d(x, y, z));
	}
}
