package edu.flash3388.flashlib.util;

public final class Algorithms {
	private Algorithms(){}
	
	public static interface VoidAction<T>{
		void execute(T object);
	}
	public static interface Action<T, V>{
		V execute(T object);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sort(Object[]...as){
		if(as == null)
			throw new NullPointerException("Array is null");
		if(as.length < 1) 
			throw new IllegalArgumentException("Less than 1 array was passed");
		
		Object[] main = as[0];
		for (int i = 0; i < main.length; i++) {
			for (int j = 0; j < main.length-1; j++) {
				if(((Comparable)main[j]).compareTo(main[j+1]) > 0){
					for (int j2 = 0; j2 < as.length; j2++) {
						Object t = as[j2][j];
						as[j2][j] = as[j2][j+1];
						as[j2][j+1] = t;
					}
				}
			}
		}
	}
	public static void mergeSort(int from, int to, Object[]...as){
		if(as == null)
			throw new NullPointerException("Array is null");
		if(as.length < 1) 
			throw new IllegalArgumentException("Less than 1 array was passed");
	}
	
	public static <T> TreeNode<T> minMax(TreeNode<T> head, 
			Action<T, ?extends TreeNode<T>> action, int maxDepth){
		return minMax(head, action, 0, maxDepth);
	}
	private static <T> TreeNode<T> minMax(TreeNode<T> head, 
			Action<T, ?extends TreeNode<T>> action, int depth, int maxDepth){
		if(depth >= maxDepth){
			
		}
		
		return head;
	}
}
