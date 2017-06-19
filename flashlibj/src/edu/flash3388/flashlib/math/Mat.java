package edu.flash3388.flashlib.math;

/**
 * Represents a matrix 
 * <p>
 * In mathematics, a matrix (plural matrices) is a rectangular array[1] of numbers, 
 * symbols, or expressions, arranged in rows and columns.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see <a href="https://en.wikipedia.org/wiki/Matrix_(mathematics)">https://en.wikipedia.org/wiki/Matrix_(mathematics)</a>
 */
public class Mat {

	private double[][] mat;
	
	/**
	 * Creates a new matrix with a given size.
	 * @param rows the amount of rows in the matrix
	 * @param columns the amount of columns in the matrix
	 */
	public Mat(int rows, int columns){
		mat = new double[rows][columns];
	}
	/**
	 * Creates a new matrix for a given 2-d dimensional array.
	 * @param mat the given 2-d dimensional array
	 */
	public Mat(double[][] mat){
		this.mat = mat;
	}
	
	/**
	 * Gets the amount of rows in the matrix
	 * @return the amount of rows in the matrix
	 */
	public int rows(){
		return mat.length;
	}
	/**
	 * Gets the amount of columns in the matrix
	 * @return the amount of columns in the matrix
	 */
	public int columns(){
		if(rows() > 0) return mat[0].length;
		return 0;
	}
	/**
	 * Gets the maximum value in the matrix
	 * @return the biggest value in the matrix
	 */
	public double max(){
		double max = mat[0][0];
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < columns(); j++){
				if(mat[i][j] > max)
					max = mat[i][j];
			}
		}
		return max;
	}
	
	/**
	 * Gets the 2-dimensional array used for holding values by this matrix
	 * @return the 2-dimensional array holding values in this matrix
	 */
	public double[][] get(){
		return mat;
	}
	/**
	 * Gets a value at a specific index in the matrix
	 * @param row the row index of the value
	 * @param column the column index of the value
	 * @return a value from the matrix
	 */
	public double get(int row, int column){
		return mat[row][column];
	}
	/**
	 * Gets a row from the matrix.
	 * @param row the row index
	 * @return a row from the matrix
	 */
	public double[] getRow(int row){
		return mat[row];
	}
	/**
	 * Gets a column from the matrix.
	 * @param column the row index
	 * @return a column from the matrix
	 */
	public double[] getColumn(int column){
		double[] col = new double[rows()];
		for(int i = 0; i < col.length; i++)
			col[i] = mat[i][column];
		return col;
	}
	
	/**
	 * Sets the 2-dimensional array values of the matrix.
	 * @param mat the 2-dimensional array
	 */
	public void set(double[][] mat){
		this.mat = mat;
	}
	/**
	 * Sets the value of a place in the matrix.
	 * @param row the row index
	 * @param column the column index
	 * @param val the value to set
	 */
	public void set(int row, int column, double val){
		mat[row][column] = val;
	}
	/**
	 * Sets a row in the matrix to an array.
	 * @param row the row index
	 * @param val the array to set
	 */
	public void setRow(int row, double[] val){
		mat[row] = val;
	}
	/**
	 * Sets a column in the matrix to an array.
	 * @param column the column index
	 * @param val the array to set
	 */
	public void setColumn(int column, double[] val){
		for(int i = 0; i < val.length; i++)
			mat[i][column] = val[i];
	}
	
	/**
	 * Reverses the values in this matrix by multiplying them by -1.
	 */
	public void reverse(){
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < columns(); j++)
				mat[i][j] *= -1;
		}
	}
	/**
	 * Sets all the values in this matrix to 0.
	 */
	public void reset(){
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < columns(); j++)
				mat[i][j] = 0.0;
		}
	}
	
	/**
	 * Multiplies this matrix by another and gets the result.
	 * 
	 * @param matrix the matrix to multiply by
	 * @return the result of multiplication
	 * @see IllegalArgumentException if the matrix cannot be multiplied by
	 */
	public Mat multiply(Mat matrix){
		if(columns() != matrix.rows())
			throw new IllegalArgumentException("Cannot multiply! columns do not equal to rows");
		return new Mat(Mathf.multiplyMat(mat, matrix.mat));
	}
	
	/**
	 * Transforms a 3d vector by the matrix values.
	 * 
	 * @param r the vector to transform
	 * @return a result of the transformation
	 * @throws IllegalStateException if this matrix cannot be used to transform a 3d vector.
	 */
	public Vector3 transform(Vector3 r){
		if(rows() < 4 && columns() < 4)
			throw new IllegalStateException("Matrix cannot create transform. rows() < 4 columns() < 4");
		return new Vector3(
				mat[0][0] * r.getX() + mat[0][1] * r.getY() + mat[0][2] * r.getZ() + mat[0][3],
				mat[1][0] * r.getX() + mat[1][1] * r.getY() + mat[1][2] * r.getZ() + mat[1][3],
				mat[2][0] * r.getX() + mat[2][1] * r.getY() + mat[2][2] * r.getZ() + mat[2][3]);
	}
	
	/**
	 * Converts this matrix to a 3d vector. Uses values from [0][0], [1][0], [2][0].
	 * @return a new vector with values from the matrix
	 * @throws IllegalStateException if the matrix cannot be used to provide values for the vector
	 */
	public Vector3 toVector3(){
		if(rows() < 3 && columns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 columns() < 1");
		return new Vector3(mat[0][0], mat[1][0], mat[2][0]);
	}
	/**
	 * Converts this matrix to a 2d vector. Uses values from [0][0], [1][0].
	 * @return a new vector with values from the matrix
	 * @throws IllegalStateException if the matrix cannot be used to provide values for the vector
	 */
	public Vector2 toVector2(){
		if(rows() < 2 && columns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 columns() < 1");
		return new Vector2(mat[0][0], mat[1][0]);
	}
	
	/**
	 * Sets the coordinates of a given vector to values [0][0], [1][0], [2][0] from the matrix.
	 * @param vector the vector
	 * @return the given vector
	 * @throws IllegalStateException if the matrix cannot be used to provide values for the vector
	 */
	public Vector3 fill(Vector3 vector){
		if(rows() < 3 && columns() < 1)
			throw new IllegalStateException("Matrix cannot convert to vector3. rows() < 3 columns() < 1");
		vector.set(mat[0][0], mat[1][0], mat[2][0]);
		return vector;
	}
	/**
	 * Sets the coordinates of a given vector to values [0][0], [1][0] from the matrix.
	 * @param vector the vector
	 * @return the given vector
	 * @throws IllegalStateException if the matrix cannot be used to provide values for the vector
	 */
	public Vector2 fill(Vector2 vector){
		if(rows() != 2 && columns() != 1)
			throw new IllegalStateException("Matrix cannot convert to vector2. rows() < 2 columns() < 1");
		vector.set(mat[0][0], mat[1][0]);
		return vector;
	}
	
	/**
	 * Gets whether or not this matrix equals to a given matrix.
	 * @param mat matrix to compare to
	 * @return true if the matrices are equal, false otherwise
	 */
	public boolean equals(Mat mat){
		return equals(mat.mat);
	}
	/**
	 * Gets whether or not this matrix equals to a given 2-d array.
	 * @param mat2 2-d array to compare to
	 * @return true if the matrices are equal, false otherwise
	 */
	public boolean equals(double[][] mat2){
		if(rows() != mat2.length || columns() != mat2[0].length) return false;
		for(int i = 0; i < rows(); i++){
			for(int j = 0; j < columns(); j++)
				if(mat[i][j] != mat2[i][j])
					return false;
		}
		return true;
	}
	
	/**
	 * Creates a copy of this matrix and returns it.
	 * @return a copy of this matrix
	 */
	public Mat copy(){
		double[][] m = new double[rows()][columns()];
		for(int i = 0; i < m.length; i++){
			for(int j = 0; j < m[0].length; j++)
				m[i][j] = mat[i][j];
		}
		return new Mat(m);
	}
}
