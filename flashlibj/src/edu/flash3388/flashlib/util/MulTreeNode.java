package edu.flash3388.flashlib.util;

public class MulTreeNode<T> implements TreeNode<T>{

	private T value;
	private TreeNode<?>[] nodes;
	
	public MulTreeNode(T value, int initialCapacity){
		this.value = value;
		nodes = new TreeNode<?>[initialCapacity];
	}
	public MulTreeNode(T value){
		this(value, 10);
	}
	
	private void expand(int n){
		TreeNode<?>[] newnodes = new TreeNode<?>[n + 5];
		System.arraycopy(nodes, 0, newnodes, 0, nodes.length);
		nodes = newnodes;
	}
	
	@Override
	public T getValue() {
		return value;
	}
	@Override
	public void setValue(T value) {
		this.value = value;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public TreeNode<T> getChild(int index) {
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		if(index >= nodes.length)
			return null;
		return (TreeNode<T>) nodes[index];
	}
	@Override
	public void setChild(TreeNode<T> node, int index) {
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		if(index >= nodes.length)
			expand(index);
		nodes[index] = node;
	}
	@Override
	public boolean hasChild(int index) {
		if(index < 0) 
			throw new IllegalArgumentException("Index must be non-negative");
		if(index >= nodes.length)
			return false;
		return nodes[index] != null;
	}
	@SuppressWarnings("unchecked")
	@Override
	public TreeNode<T>[] getChildren() {
		TreeNode<?>[] rNodes = new TreeNode<?>[nodes.length];
		System.arraycopy(nodes, 0, rNodes, 0, nodes.length);
		return (TreeNode<T>[]) rNodes;
	}
}
