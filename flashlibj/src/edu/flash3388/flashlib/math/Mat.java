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
		double[][] result = new double[matrix.rows()][matrix.coloumns()];
		for(int i = 0; i < result[0].length; i++){
			for(int j = 0; j < result.length; j++){
				double value = 0;
				for(int k = 0; k < result.length; k++)
					value += mat[j][k] * matrix.mat[k][i];
				result[j][i] = value;
			}
		}
		return new Mat(result);
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
		Mat rx = new Mat(4, 4);
		Mat ry = new Mat(4, 4);
		Mat rz = new Mat(4, 4);
		
		x = Math.toRadians(x);
		y = Math.toRadians(y);
		z = Math.toRadians(z);
		
		rz.mat[0][0] = Math.cos(z);rz.mat[0][1] = -Math.sin(z);rz.mat[0][2] = 0;				rz.mat[0][3] = 0;
		rz.mat[1][0] = Math.sin(z);rz.mat[1][1] = Math.cos(z);rz.mat[1][2] = 0;					rz.mat[1][3] = 0;
		rz.mat[2][0] = 0;					rz.mat[2][1] = 0;					rz.mat[2][2] = 1;					rz.mat[2][3] = 0;
		rz.mat[3][0] = 0;					rz.mat[3][1] = 0;					rz.mat[3][2] = 0;					rz.mat[3][3] = 1;
		
		rx.mat[0][0] = 1;					rx.mat[0][1] = 0;					rx.mat[0][2] = 0;					rx.mat[0][3] = 0;
		rx.mat[1][0] = 0;					rx.mat[1][1] = Math.cos(x);rx.mat[1][2] = -Math.sin(x);rx.mat[1][3] = 0;
		rx.mat[2][0] = 0;					rx.mat[2][1] = Math.sin(x);rx.mat[2][2] = Math.cos(x);rx.mat[2][3] = 0;
		rx.mat[3][0] = 0;					rx.mat[3][1] = 0;					rx.mat[3][2] = 0;					rx.mat[3][3] = 1;
		
		ry.mat[0][0] = Math.cos(y);ry.mat[0][1] = 0;					ry.mat[0][2] = -Math.sin(y);ry.mat[0][3] = 0;
		ry.mat[1][0] = 0;					ry.mat[1][1] = 1;					ry.mat[1][2] = 0;					ry.mat[1][3] = 0;
		ry.mat[2][0] = Math.sin(y);ry.mat[2][1] = 0;					ry.mat[2][2] = Math.cos(y);ry.mat[2][3] = 0;
		ry.mat[3][0] = 0;					ry.mat[3][1] = 0;					ry.mat[3][2] = 0;					ry.mat[3][3] = 1;
		
		return new Mat(rz.multiply(ry.multiply(rx)).get());
	}
}
