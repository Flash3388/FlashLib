package edu.flash3388.flashlib.util;

public class BinTreeNode<T> implements TreeNode<T>{

	private T value;
	private TreeNode<T> nodeLeft, nodeRight;
	
	public BinTreeNode(T value, TreeNode<T> left, TreeNode<T> right){
		this.value = value;
		this.nodeRight = right;
		this.nodeLeft = left;
	}
	public BinTreeNode(T value){
		this(value, null, null);
	}
	
	@Override
	public T getValue() {
		return value;
	}
	@Override
	public void setValue(T value) {
		this.value = value;
	}

	public TreeNode<T> getLeft(){
		return getChild(0);
	}
	public void setLeft(TreeNode<T> node){
		setChild(node, 0);
	}
	public boolean hasLeft(){
		return hasChild(0);
	}
	public TreeNode<T> getRight(){
		return getChild(1);
	}
	public void setRight(TreeNode<T> node){
		setChild(node, 1);
	}
	public boolean hasRight(){
		return hasChild(1);
	}
	
	@Override
	public TreeNode<T> getChild(int index) {
		if(index < 0 || index >= 2) 
			throw new IndexOutOfBoundsException("Index out of bounds");
		return index == 0? nodeLeft : nodeRight;
	}
	@Override
	public void setChild(TreeNode<T> node, int index) {
		if(index < 0 || index >= 2) 
			throw new IndexOutOfBoundsException("Index out of bounds");
		if(index == 0)
			nodeLeft = node;
		else
			nodeRight = node;
	}
	@Override
	public boolean hasChild(int index) {
		if(index < 0 || index >= 2) 
			throw new IndexOutOfBoundsException("Index out of bounds");
		return index == 0? nodeLeft != null : nodeRight != null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public TreeNode<T>[] getChildren() {
		return (TreeNode<T>[]) new TreeNode<?>[]{nodeLeft, nodeRight};
	}
}
