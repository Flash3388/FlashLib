package edu.flash3388.flashlib.util;

public interface TreeNode<T> {
	T getValue();
	void setValue(T value);
	TreeNode<T> getChild(int index);
	void setChild(TreeNode<T> node, int index);
	boolean hasChild(int index);
	TreeNode<T>[] getChildren();
}
